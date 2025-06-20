package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CartItemWithProductDTO;
import com.example.demo.dto.CartItemWithUserDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.CartItemNotFoundException;
import com.example.demo.feign.ProductClient;
import com.example.demo.feign.UserClient;
import com.example.demo.model.CartItem;
import com.example.demo.repository.CartItemRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemServiceImpl implements CartItemService {

	private static final Logger log = LoggerFactory.getLogger(CartItemServiceImpl.class);

	@Autowired
	private ProductClient productClient;

	@Autowired
	CartItemRepository repository;

	@Autowired
	private UserClient userClient;

	// Adds a new cart item to the repository.
	// Fetches product details to calculate the total price.
	// Logs the operation and saves the cart item.
	@Override
	public String addCartItem(CartItem cartItem) {
		log.info("Adding cart item for productId={} and userId={}", cartItem.getProductId(), cartItem.getUserId());
		ProductDTO product = productClient.getProductById(cartItem.getProductId());
		double totalPrice = product.getPrice() * cartItem.getQuantity();
		cartItem.setTotalPrice(totalPrice);
		repository.save(cartItem);
		log.info("Cart item added successfully with totalPrice={}", totalPrice);
		return "Item added to cart successfully.";
	}

	// Updates an existing cart item in the repository.
	// Logs the update attempt.
	// Returns the updated cart item.
	@Override
	public CartItem updateCartItem(CartItem cartItem) {
		log.info("Updating cart item with ID: {}", cartItem.getCartItemId());
		// Fetch the product to get its current price
		ProductDTO product = productClient.getProductById(cartItem.getProductId());
		// Recalculate total price based on the new quantity and product's current price
		double newTotalPrice = product.getPrice() * cartItem.getQuantity();
		cartItem.setTotalPrice(newTotalPrice);
		return repository.save(cartItem);
	}

	// Retrieves a cart item by its ID from the repository.
	// Throws an exception if the cart item is not found.
	@Override
	public CartItem getCartItemById(int cartItemId) {
		log.info("Fetching cart item with ID: {}", cartItemId);
		return repository.findById(cartItemId)
				.orElseThrow(() -> new CartItemNotFoundException("Cart item not found with ID: " + cartItemId));
	}

	// Retrieves all cart items from the repository.
	// Logs the total number of items retrieved.
	@Override
	public List<CartItem> getAllCartItems() {
		log.info("Fetching all cart items");
		List<CartItem> items = repository.findAll();
		log.info("Total cart items fetched: {}", items.size());
		return items;
	}

	// Deletes a cart item by its ID if it exists in the repository.
	// Logs the attempt and throws an exception if not found.
	@Override
	public String deleteCartItemById(int cartItemId) {
		log.info("Attempting to delete cart item with ID: {}", cartItemId);
		if (!repository.existsById(cartItemId)) {
			log.warn("Cart item not found for ID: {}", cartItemId);
			throw new CartItemNotFoundException("Cannot delete. Cart item not found with ID: " + cartItemId);
		}
		repository.deleteById(cartItemId);
		log.info("Cart item deleted: {}", cartItemId);
		return "Cart item deleted.";

	}

	// Retrieves cart items associated with a specific user ID.
	// Logs the retrieval attempt and number of items found.
	@Override
	public List<CartItem> getCartItemsByUserId(int userId) {
		log.info("Fetching cart items for user ID: {}", userId);
		List<CartItem> items = repository.findByUserId(userId);
		log.info("Total cart items for user {}: {}", userId, items.size());
		return items;
	}

	// Retrieves product details for a given product ID using a Feign client.
	@Override
	public ProductDTO fetchProductDetails(int productId) {
		log.info("Fetching product details for product ID: {}", productId);
		return productClient.getProductById(productId);
	}

	// Retrieves cart item details along with its associated product information.
	// Throws an exception if the cart item is not found.
	@Override
	public CartItemWithProductDTO getCartItemWithProduct(int cartItemId) {
		log.info("Fetching cart item with product for cartItemId={}", cartItemId);
		CartItem cartItem = repository.findById(cartItemId).orElseThrow(() -> {
			log.error("CartItem not found: {}", cartItemId);
			return new RuntimeException("CartItem not found");
		});

		ProductDTO productDTO = productClient.getProductById(cartItem.getProductId());

		CartItemWithProductDTO dto = new CartItemWithProductDTO();
		dto.setCartItemId(cartItem.getCartItemId());
		dto.setProductId(cartItem.getProductId());
		dto.setQuantity(cartItem.getQuantity());
		dto.setProduct(productDTO);

		log.info("Returning cart item with product details");
		return dto;
	}

	// Retrieves cart items along with their associated product details for a
	// specific user ID.
	// Uses Java Streams to map cart items to DTO objects.
	@Override
	public List<CartItemWithProductDTO> getCartItemsWithProductsByUserId(int userId) {
		log.info("Fetching cart items with products for userId={}", userId);
		List<CartItem> cartItems = repository.findByUserId(userId);

		return cartItems.stream().map(item -> {
			ProductDTO productDTO = productClient.getProductById(item.getProductId());

			CartItemWithProductDTO dto = new CartItemWithProductDTO();
			dto.setCartItemId(item.getCartItemId());
			dto.setProductId(item.getProductId());
			dto.setQuantity(item.getQuantity());
			dto.setProduct(productDTO);

			return dto;
		}).collect(Collectors.toList());
	}

	// Retrieves cart items associated with a user, including user details.
	// Iterates through cart items to populate DTO objects with user information.
	@Override
	public List<CartItemWithUserDTO> getCartItemsWithUser(int userId) {
		log.info("Fetching cart items with user for userId={}", userId);
		List<CartItem> cartItems = repository.findByUserId(userId);
		UserDTO user = userClient.getUserById(userId);

		List<CartItemWithUserDTO> result = new ArrayList<>();
		for (CartItem item : cartItems) {
			CartItemWithUserDTO dto = new CartItemWithUserDTO();
			dto.setCartItemId(item.getCartItemId());
			dto.setUserId(item.getUserId());
			dto.setProductId(item.getProductId());
			dto.setQuantity(item.getQuantity());
			dto.setTotalPrice(item.getTotalPrice());
			dto.setUser(user);

			result.add(dto);
		}

		log.info("Returning {} cart items with user info", result.size());
		return result;
	}

	@Override
    @Transactional // Ensures atomicity for clearing multiple cart items
    public String clearCartByUserId(int userId) {
        log.info("Attempting to clear cart for user ID: {}", userId);
        List<CartItem> cartItems = repository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            log.info("Cart is already empty for user ID: {}", userId);
            return "Cart is already empty.";
        }
        repository.deleteAll(cartItems); // Deletes all found cart items for the user
        log.info("Cart cleared successfully for user ID: {}. Deleted {} items.", userId, cartItems.size());
        return "Cart cleared successfully.";
    }
}