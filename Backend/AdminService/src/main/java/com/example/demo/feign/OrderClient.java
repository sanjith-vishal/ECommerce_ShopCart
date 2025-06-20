package com.example.demo.feign;

import com.example.demo.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ORDERSERVICE")
public interface OrderClient {
	@GetMapping("/order/fetchAll")
	List<OrderDTO> getAllOrders();

	@DeleteMapping("/order/delete/{id}")
	String deleteOrder(@PathVariable int id);
}