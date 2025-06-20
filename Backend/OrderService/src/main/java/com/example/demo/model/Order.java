package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int orderId;

	@NotNull(message = "UserID is required")
	private int userId;

	@Min(value = 0, message = "Total price must be 0 or more")
	private double totalPrice;

	@NotBlank(message = "Shipping address is required")
	private String shippingAddress;

	@NotBlank(message = "Order status is required")
	private String orderStatus;

	@NotBlank(message = "Payment status is required")
	private String paymentStatus;
	
	// Add order date
    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    // One Order can have many OrderItems
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    private List<OrderItem> orderItems; // Collection of items in this order
}