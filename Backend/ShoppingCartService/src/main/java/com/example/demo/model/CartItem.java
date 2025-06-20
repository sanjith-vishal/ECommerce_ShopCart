package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cartItemId;

	private int userId;

	@NotNull(message = "ProductID is required")
	private int productId;

	@Min(value = 1, message = "Quantity must be at least 1")
	private int quantity;

	// @Min(value = 0, message = "Total price must be 0 or more")
	private double totalPrice;
}