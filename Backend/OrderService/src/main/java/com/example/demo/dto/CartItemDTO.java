package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
	private int cartItemId;
	private int productId;
	private int userId;
	private int quantity;
	private double totalPrice;
}