package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInOrderDTO {
    private int productId;
    private String productName;
    private double priceAtOrder;
    private int quantity;
}