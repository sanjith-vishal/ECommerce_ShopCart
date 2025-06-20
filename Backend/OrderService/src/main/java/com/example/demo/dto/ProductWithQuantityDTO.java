package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithQuantityDTO {
	private int productId;
	private String productName;
	private String description;
	private double price;
	private int quantity;
}