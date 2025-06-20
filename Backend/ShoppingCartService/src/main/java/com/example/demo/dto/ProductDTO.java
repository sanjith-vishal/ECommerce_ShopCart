package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private int productId;
	private String productName;
	private String description;
	private double price;
	private String category;
	private String imageURL;
	private int quantity;
}