package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    // Many OrderItems can belong to one Order
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("orderItems")
    private Order order; // Reference to the parent Order

    private int productId; // ID of the product
    private String productName; // Store product name for historical accuracy (product might change)
    private double priceAtOrder; // Store price at the time of order
    private int quantity; // Quantity of this product in the order
    // You might also want to store other product details like imageURL, description, etc.
    // or fetch them from the ProductService using productId if they rarely change.
    // Storing them directly here makes the order immutable for reporting/history.
}