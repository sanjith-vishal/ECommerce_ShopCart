package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.OrderSummaryDTO;
import com.example.demo.dto.OrderWithProductDTO;
import com.example.demo.dto.OrderWithProductDetailsDTO;
import com.example.demo.model.Order;

public interface OrderService {

	public abstract String placeOrder(Order order);

	public abstract Order updateOrder(Order order);

	public abstract List<OrderWithProductDetailsDTO> getAllOrders();

	public abstract String deleteOrderById(int orderId);

	public abstract List<OrderWithProductDetailsDTO> getOrdersByUserId(int userId);

	public abstract List<Order> getOrdersByOrderStatus(String status);

	public abstract List<Order> getOrdersByPaymentStatus(String status);

	public abstract String placeOrderByUserId(int userId, Order orderDetails);

	public abstract OrderWithProductDTO getOrderWithProductDetails(int orderId);
}
