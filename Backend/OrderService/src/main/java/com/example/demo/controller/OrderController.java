package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.OrderSummaryDTO;
import com.example.demo.dto.OrderWithProductDTO;
import com.example.demo.dto.OrderWithProductDetailsDTO;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService service;

	@PostMapping("/place")
	public String placeOrder(@Valid @RequestBody Order order) {
		return service.placeOrder(order);
	}

	@PutMapping("/update")
	public Order updateOrder(@RequestBody Order order) {
		return service.updateOrder(order);
	}

	@GetMapping("/fetchAll")
	public List<OrderWithProductDetailsDTO> getAllOrders() {
		return service.getAllOrders();
	}
 
	@DeleteMapping("/delete/{id}")
	public String deleteOrder(@PathVariable("id") int id) {
		return service.deleteOrderById(id);
	}

	@GetMapping("/byUser/{userId}")
	public List<OrderWithProductDetailsDTO> getOrdersWithProductDetailsByUserId(@PathVariable int userId) {
		return service.getOrdersByUserId(userId);
	}

	@GetMapping("/byStatus/{status}")
	public List<Order> getByOrderStatus(@PathVariable String status) {
		return service.getOrdersByOrderStatus(status);
	}

	@GetMapping("/byPaymentStatus/{status}")
	public List<Order> getByPaymentStatus(@PathVariable String status) {
		return service.getOrdersByPaymentStatus(status);
	}

	@PostMapping("/placeOrderByUserId/{userId}")
	public ResponseEntity<String> placeOrder(@PathVariable("userId") int userId, @RequestBody Order orderDetails) {
		return ResponseEntity.ok(service.placeOrderByUserId(userId, orderDetails));
	}

	@GetMapping("/orderWithProductDetails/{orderId}")
	public ResponseEntity<OrderWithProductDTO> getOrderWithProductDetails(@PathVariable int orderId) {
		return ResponseEntity.ok(service.getOrderWithProductDetails(orderId));
	}

}
