import { CommonModule, CurrencyPipe, NgFor } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../cart.service';
import { CommonService } from '../common.service';
import { OrdersService } from '../orders.service';

declare var Razorpay: any;

// Interface for Toast messages
interface Toast {
  id: number;
  message: string;
  type: string; // e.g., 'success', 'danger', 'warning', 'info'
}

@Component({
  selector: 'app-cart',
  imports: [CommonModule, NgFor, FormsModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit{

  cartItems: any[] = [];
  loggedInUserId: number | null = null;
  shippingCost: number = 0;
  username: string | null = null;
  shippingAddress: string = ''; 

  // Toast properties
  toasts: Toast[] = [];
  private nextToastId: number = 0; // To give unique IDs to toasts

  constructor(private cartService: CartService, private commonService: CommonService, private router: Router, private orderService: OrdersService) { }

  ngOnInit(): void {
    this.loggedInUserId = this.commonService.getLoggedInUserId();
    this.username = this.commonService.getUsername();
    if (this.loggedInUserId) {
      this.fetchCartItems();
      this.loadRazorpayScript();
    } else {
      console.warn('User not logged in. Cannot fetch cart items.');
      this.showToast('warning', 'Please log in to view your cart.'); // Using toast
      this.router.navigate(['/login']);
    }
  }

  loadRazorpayScript() {
    if (typeof Razorpay === "undefined") {
      const script = document.createElement("script");
      script.src = "https://checkout.razorpay.com/v1/checkout.js";
      script.onload = () => {
        console.log("Razorpay script loaded successfully");
      };
      script.onerror = (error) => {
        console.error("Failed to load Razorpay script:", error);
        this.showToast('danger', "Failed to load payment gateway. Please try again."); // Using toast
      };
      document.body.appendChild(script);
    }
  }

  // --- Toast Management ---
  showToast(type: string, message: string): void {
    const id = this.nextToastId++;
    this.toasts.push({ id, message, type });

    // Automatically remove the toast after 3 seconds
    setTimeout(() => {
      this.removeToast(id);
    }, 3000);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(toast => toast.id !== id);
  }

  fetchCartItems(): void {
    if (this.loggedInUserId) {
      this.cartService.getCartItemsByUserId(this.loggedInUserId).subscribe({
        next: (data: any[]) => {
          this.cartItems = data;
          console.log('Cart items fetched:', this.cartItems);
        },
        error: (err) => {
          console.error('Error fetching cart items:', err);
          this.showToast('danger', 'Failed to load cart items. Please try again.'); // Using toast
          this.cartItems = [];
        }
      });
    }
  }

  calculateSubtotal(): number {
    return this.cartItems.reduce((total, item) => total + (item.product.price * item.quantity), 0);
  }

  updateQuantity(item: any): void {
    if (item.quantity < 1) {
      item.quantity = 1;
      this.showToast('warning', 'Quantity cannot be less than 1. Set to 1.'); // Using toast for quantity validation
      return;
    }

    const updatedCartItem = {
      cartItemId: item.cartItemId,
      userId: this.loggedInUserId!,
      productId: item.productId,
      quantity: item.quantity,
      totalPrice: item.product.price * item.quantity
    };

    this.cartService.updateCartItem(updatedCartItem).subscribe({
      next: (res) => {
        console.log('Cart item quantity updated:', res);
        this.fetchCartItems();
        this.showToast('success', 'Item quantity updated successfully!'); // Using toast
      },
      error: (err) => {
        console.error('Error updating cart item quantity:', err);
        this.showToast('danger', 'Failed to update quantity: ' + (err.error?.message || err.message || 'Server error.')); // Using toast
        this.fetchCartItems();
      }
    });
  }

  removeItem(cartItemId: number): void {
    if (confirm('Are you sure you want to remove this item from your cart?')) {
      this.cartService.removeItemFromCart(cartItemId).subscribe({
        next: (res: string) => {
          console.log('Item removed:', res);
          this.showToast('info', res); // Using toast
          this.fetchCartItems();
        },
        error: (err) => {
          console.error('Error removing item from cart:', err);
          this.showToast('danger', 'Failed to remove item: ' + (err.error?.message || err.message || 'Server error.')); // Using toast
        }
      });
    }
  }

  clearCart(): void {
    if (this.loggedInUserId && confirm('Are you sure you want to clear your entire cart?')) {
      this.cartService.clearCartByUserId(this.loggedInUserId).subscribe({
        next: (res: string) => {
          console.log('Cart cleared:', res);
          this.showToast('info', res); // Using toast
          this.fetchCartItems();
        },
        error: (err) => {
          console.error('Error clearing cart:', err);
          this.showToast('danger', 'Failed to clear cart: ' + (err.error?.message || err.message || 'Server error.')); // Using toast
        }
      });
    }
  }

  // --- Checkout and Payment Integration ---
  initiateCheckout(): void {
    if (!this.loggedInUserId) {
      this.showToast('warning', 'Please log in to proceed with payment.'); // Using toast
      this.router.navigate(['/login']);
      return;
    }

    if (this.cartItems.length === 0) {
      this.showToast('warning', 'Your cart is empty. Please add items before proceeding to checkout.'); // Using toast
      return;
    }

    if (!this.shippingAddress.trim()) {
      this.showToast('warning', 'Please enter your shipping address to proceed.'); // Using toast
      return;
    }

    const totalAmount = this.calculateSubtotal() + this.shippingCost;

    this.openRazorpay(totalAmount);
  }

  openRazorpay(amount: number): void {
    if (typeof Razorpay === "undefined") {
      this.showToast('danger', "Payment gateway is not loaded. Please try again."); // Using toast
      return;
    }

    const options = {
      key: 'rzp_test_mMYCMsqoLV36CI',
      amount: amount * 100,
      currency: 'INR',
      name: 'ShopCart',
      description: 'Cart Checkout Payment',
      image: 'pic2.jpg',
      handler: (response: any) => {
        console.log('Razorpay payment successful:', response);
        this.processPaymentSuccess(response.razorpay_payment_id);
      },
      prefill: {
        name: this.commonService.username || 'Customer Name',
        email: 'customer@example.com',
        contact: '9876543210'
      },
      theme: {
        color: '#4CAF50' // Using the primary color from your new theme
      }
    };

    const rzp = new Razorpay(options);
    rzp.on('payment.failed', (response: any) => {
      console.error('Razorpay payment failed:', response);
      this.showToast('danger', 'Payment failed. Please try again. Error: ' + response.error.description); // Using toast
    });
    rzp.open();
  }

  processPaymentSuccess(transactionId: string): void {
    console.log("Payment successful with transaction ID:", transactionId);

    if (!this.loggedInUserId) {
      this.showToast('warning', "User not logged in. Cannot place order."); // Using toast
      this.router.navigate(['/login']);
      return;
    }

    const orderDetails: any = {
      userId: this.loggedInUserId,
      totalPrice: this.calculateSubtotal() + this.shippingCost,
      shippingAddress: this.shippingAddress,
      orderStatus: 'Placed',
      paymentStatus: 'Paid',
    };

    this.orderService.placeOrder(this.loggedInUserId, orderDetails).subscribe({
      next: (res) => {
        console.log("Order placed successfully:", res);
        this.showToast('success', "Payment Successful and Order Placed! You will be redirected to your orders."); // Using toast
        this.cartItems = [];
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        console.error("Error placing order after payment:", err);
        this.showToast('danger', "Payment successful, but order placement failed. Please contact support. Error: " + (err.error?.message || err.message || 'Server error.')); // Using toast
        this.router.navigate(['/orders']);
      }
    });
  }
}