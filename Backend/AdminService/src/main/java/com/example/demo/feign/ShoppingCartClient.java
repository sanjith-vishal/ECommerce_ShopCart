package com.example.demo.feign;

import com.example.demo.dto.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "SHOPPINGCARTSERVICE")
public interface ShoppingCartClient {
	@GetMapping("/cart/fetchAll")
	List<CartItemDTO> getAllCartItems();

	@DeleteMapping("/cart/delete/{id}")
	String deleteCartItem(@PathVariable int id);
}