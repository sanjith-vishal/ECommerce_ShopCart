package com.example.demo.dto;

import com.example.demo.model.Order;
import lombok.Data;

import java.util.List;

@Data
public class OrderWithProductDTO {
	private Order order;
	private List<ProductWithQuantityDTO> products;
}