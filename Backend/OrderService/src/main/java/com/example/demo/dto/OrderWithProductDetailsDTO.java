package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime; // Import LocalDateTime for orderDate
import java.util.List;

@Data
public class OrderWithProductDetailsDTO {
    private int orderId;
    private int userId;
    private double totalPrice;
    private String shippingAddress;
    private String orderStatus;
    private String paymentStatus;
    private LocalDateTime orderDate; // Add orderDate here

    private List<ProductInOrderDTO> items; // List of products within this order
    private UserDTO user; // User details if needed
}