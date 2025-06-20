package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderSummaryDTO {
    private int orderId;
    private int userId;
    private double totalPrice;
    private String orderStatus;
    private String paymentStatus;
    private LocalDateTime orderDate;
}