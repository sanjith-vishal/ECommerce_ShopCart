package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemWithProductDTO {
	private int cartItemId;
	private int productId;
	private int quantity;
	private ProductDTO product;
}
