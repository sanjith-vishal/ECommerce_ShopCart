package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_info")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int productId;

	@NotBlank(message = "Product name is mandatory")
	@Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
	private String productName;

	@NotBlank(message = "Description is mandatory")
	@Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
	private String description;

	@Positive(message = "Price must be positive")
	private double price;

	@NotBlank(message = "Category is mandatory")
	@Size(min = 3, max = 50, message = "Category must be between 3 and 50 characters")
	private String category;

	@NotBlank(message = "Image URL is mandatory")
	private String imageURL;

	@Min(value = 0, message = "Quantity cannot be negative")
	private int quantity;
}