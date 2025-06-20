package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
	private int orderId;
	private int userId;
	private double totalPrice;
	private String shippingAddress;
	private String orderStatus;
	private String paymentStatus;
}
