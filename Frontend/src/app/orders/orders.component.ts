import { CommonModule, DatePipe, NgFor } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OrdersService } from '../orders.service';
import { CommonService } from '../common.service';
import { FormsModule } from '@angular/forms';
import { UsersService, UserProfile } from '../users.service';
import { forkJoin, Observable } from 'rxjs';

interface DisplayOrder {
  id: number;
  date: Date;
  total: number;
  status: string;
  paymentStatus: string;
  shippingAddress: string;
  items: {
    productId: number;
    name: string;
    quantity: number;
    price: number;
  }[];
  userId: number;
  userName?: string;
  isEditingStatus?: boolean;
  selectedStatus?: string;
}

@Component({
  selector: 'app-orders',
  imports: [CommonModule, NgFor, DatePipe, RouterLink, FormsModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {

  orders: DisplayOrder[] = [];
  loggedInUserId: number | null = null;
  userRole: string | null = null;

  // Define the order of statuses for the sequential progress timeline (excluding 'Cancelled')
  orderStatuses: string[] = ['Placed', 'Processing', 'Shipped', 'Delivered'];
  // Define all possible order statuses for the admin dropdown
  orderStatusesForAdmin: string[] = ['Placed', 'Processing', 'Shipped', 'Delivered', 'Cancelled'];


  constructor(private ordersService: OrdersService, private commonService: CommonService, private usersService: UsersService) { }

  ngOnInit(): void {
    this.loggedInUserId = this.commonService.getLoggedInUserId();
    this.userRole = this.commonService.role;

    if (!this.loggedInUserId) {
      console.warn('Please log in to view your orders.');
      return;
    }

    if (this.userRole === 'ADMIN') {
      this.fetchAllOrdersForAdmin();
    } else {
      this.fetchOrdersForUser();
    }
  }

  fetchOrdersForUser(): void {
    if (this.loggedInUserId) {
      this.ordersService.getOrdersByUserId(this.loggedInUserId).subscribe({
        next: (data: any[]) => {
          console.log('Raw data from backend (getOrdersByUserId):', data);
          this.orders = this.mapOrderDetailsToDisplayOrders(data);
          console.log('Mapped user orders for display:', this.orders);
        },
        error: (err) => {
          console.error('Error fetching user orders:', err);
          this.orders = [];
        }
      });
    }
  }

  fetchAllOrdersForAdmin(): void {
    this.ordersService.getAllOrders().subscribe({
      next: (data: any[]) => {
        console.log('Raw data from backend (getAllOrders - with items):', data);

        const orderObservables: Observable<DisplayOrder>[] = data.map((orderDto: any) => {
          const baseOrder: DisplayOrder = {
            id: orderDto.orderId,
            date: new Date(orderDto.orderDate),
            total: orderDto.totalPrice,
            status: orderDto.orderStatus,
            paymentStatus: orderDto.paymentStatus,
            shippingAddress: orderDto.shippingAddress,
            userId: orderDto.userId,
            items: orderDto.items.map((item: any) => ({
              productId: item.productId,
              name: item.productName,
              quantity: item.quantity,
              price: item.priceAtOrder
            })),
            isEditingStatus: false,
            selectedStatus: orderDto.orderStatus
          };

          return this.usersService.getUserById(orderDto.userId).pipe(
            (obs) => {
              return new Observable<DisplayOrder>(subscriber => {
                obs.subscribe({
                  next: (userProfile: UserProfile) => {
                    baseOrder.userName = userProfile.name;
                    subscriber.next(baseOrder);
                    subscriber.complete();
                  },
                  error: (userError) => {
                    console.warn(`Could not fetch user name for userId ${orderDto.userId}:`, userError);
                    baseOrder.userName = 'Unknown User';
                    subscriber.next(baseOrder);
                    subscriber.complete();
                  }
                });
              });
            }
          );
        });

        forkJoin(orderObservables).subscribe({
          next: (processedOrders: DisplayOrder[]) => {
            this.orders = processedOrders;
            console.log('Mapped all orders for admin (with user names):', this.orders);
          },
          error: (err) => {
            console.error('Error processing orders with user names:', err);
            this.orders = [];
          }
        });
      },
      error: (err) => {
        console.error('Error fetching all orders for admin:', err);
        this.orders = [];
      }
    });
  }

  mapOrderDetailsToDisplayOrders(data: any[]): DisplayOrder[] {
    return data.map((orderDto: any) => ({
        id: orderDto.orderId,
        date: new Date(orderDto.orderDate),
        total: orderDto.totalPrice,
        status: orderDto.orderStatus,
        paymentStatus: orderDto.paymentStatus,
        shippingAddress: orderDto.shippingAddress,
        userId: orderDto.userId,
        items: orderDto.items.map((item: any) => ({
            productId: item.productId,
            name: item.productName,
            quantity: item.quantity,
            price: item.priceAtOrder
        })),
        isEditingStatus: false,
        selectedStatus: orderDto.orderStatus
    }));
  }

  toggleEditStatus(order: DisplayOrder): void {
    order.isEditingStatus = !order.isEditingStatus;
    if (order.isEditingStatus) {
      order.selectedStatus = order.status;
    }
  }

  updateOrderStatus(order: DisplayOrder): void {
    if (order.selectedStatus) {
      const updatedOrderPayload: any = {
        orderId: order.id,
        orderStatus: order.selectedStatus
      };

      this.ordersService.updateOrder(updatedOrderPayload).subscribe({
        next: (res: any) => {
          console.log('Order status updated successfully:', res);
          order.status = res.orderStatus;
          order.isEditingStatus = false;
        },
        error: (err) => {
          console.error('Error updating order status:', err);
          const errorMessage = err.error?.message || 'Server error. Please check backend logs.';
          console.error('Failed to update order status: ' + errorMessage);
        }
      });
    }
  }

  /**
   * Determines if a timeline status is currently active for the given order.
   * This is only for the sequential timeline, so 'Cancelled' is not considered.
   * @param currentOrderStatus The actual status of the order.
   * @param timelineStatus The status of the current step in the timeline.
   * @param allStatuses The complete ordered list of sequential statuses (excluding 'Cancelled').
   * @returns True if the timelineStatus matches the currentOrderStatus and the order is not cancelled.
   */
  isStatusActive(currentOrderStatus: string, timelineStatus: string, allStatuses: string[]): boolean {
    return currentOrderStatus === timelineStatus && currentOrderStatus !== 'Cancelled';
  }

  /**
   * Determines if a timeline status is completed for the given order.
   * This is only for the sequential timeline, so 'Cancelled' is not considered a completed step in this flow.
   * @param currentOrderStatus The actual status of the order.
   * @param timelineStatus The status of the current step in the timeline.
   * @param allStatuses The complete ordered list of sequential statuses (excluding 'Cancelled').
   * @returns True if the timelineStatus is considered completed and the order is not cancelled.
   */
  isStatusCompleted(currentOrderStatus: string, timelineStatus: string, allStatuses: string[]): boolean {
    if (currentOrderStatus === 'Cancelled') {
      return false; // No steps are completed if the order is cancelled, for the sequential tracker
    }

    const currentIndex = allStatuses.indexOf(currentOrderStatus);
    const timelineIndex = allStatuses.indexOf(timelineStatus);
    return timelineIndex <= currentIndex;
  }
}