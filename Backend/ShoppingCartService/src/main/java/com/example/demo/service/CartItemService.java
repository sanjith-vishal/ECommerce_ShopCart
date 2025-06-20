package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CartItemWithProductDTO;
import com.example.demo.dto.CartItemWithUserDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.model.CartItem;

public interface CartItemService {

	public abstract String addCartItem(CartItem item);

	public abstract CartItem updateCartItem(CartItem item);

	public abstract CartItem getCartItemById(int cartItemId);

	public abstract List<CartItem> getAllCartItems();

	public abstract String deleteCartItemById(int cartItemId);

	public abstract List<CartItem> getCartItemsByUserId(int userId);

	public abstract ProductDTO fetchProductDetails(int productId);

	public abstract CartItemWithProductDTO getCartItemWithProduct(int cartItemId);

	public abstract List<CartItemWithProductDTO> getCartItemsWithProductsByUserId(int userId);

	public abstract List<CartItemWithUserDTO> getCartItemsWithUser(int userId);
	
	public abstract String clearCartByUserId(int userId);
}