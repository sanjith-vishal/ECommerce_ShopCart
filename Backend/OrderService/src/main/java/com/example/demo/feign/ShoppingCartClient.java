package com.example.demo.feign;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.CartItemWithProductDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "SHOPPINGCARTSERVICE")
public interface ShoppingCartClient {
	@GetMapping("/cart/fetchByUserId/{userId}")
	List<CartItemDTO> getCartItemsByUserId(@PathVariable("userId") int userId);

	@GetMapping("/cart/cartItems/withProducts/{userId}")
	List<CartItemWithProductDTO> getCartItemsWithProducts(@PathVariable("userId") int userId);
	
	@DeleteMapping("/cart/clearByUserId/{userId}")
    String clearCartByUserId(@PathVariable("userId") int userId);

}


