package com.example.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CartItemWithProductDTO;
import com.example.demo.dto.CartItemWithUserDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.model.CartItem;
import com.example.demo.service.CartItemService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartItemController {

	CartItemService service;

	@PostMapping("/add")
	public String addToCart(@Valid @RequestBody CartItem cartItem) {
		return service.addCartItem(cartItem);
	}

	@PutMapping("/update")
	public CartItem updateCart(@Valid @RequestBody CartItem cartItem) {
		return service.updateCartItem(cartItem);
	}

	@GetMapping("/fetchByCartItemId/{id}")
	public CartItem getCartItem(@PathVariable("id") int id) {
		return service.getCartItemById(id);
	}

	@GetMapping("/fetchAll")
	public List<CartItem> getAllCartItems() {
		return service.getAllCartItems();
	}

	@DeleteMapping("/delete/{id}")
	public String deleteCartItem(@PathVariable("id") int id) {
		return service.deleteCartItemById(id);
	}

	@GetMapping("/fetchByUserId/{userId}")
	public List<CartItem> getByProductId(@PathVariable("userId") int userId) {
		return service.getCartItemsByUserId(userId);
	}

	@GetMapping("/fetchProductDetails/{productId}")
	public ProductDTO fetchProductDetails(@PathVariable("productId") int productId) {
		return service.fetchProductDetails(productId);
	}

	@GetMapping("/cartItem/withProduct/{cartItemId}")
	public ResponseEntity<CartItemWithProductDTO> getCartItemWithProduct(@PathVariable("cartItemId") int cartItemid) {
		return ResponseEntity.ok(service.getCartItemWithProduct(cartItemid));
	}

	@GetMapping("/cartItems/withProducts/{userId}")
	public ResponseEntity<List<CartItemWithProductDTO>> getCartItemsWithProducts(@PathVariable("userId") int userId) {
		return ResponseEntity.ok(service.getCartItemsWithProductsByUserId(userId));
	}

	@GetMapping("/cartItemsWithUser/{userId}")
	public List<CartItemWithUserDTO> getCartItemsWithUser(@PathVariable("userId") int userId) {
		return service.getCartItemsWithUser(userId);
	}
	
	@DeleteMapping("/clearByUserId/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(service.clearCartByUserId(userId));
    }
}