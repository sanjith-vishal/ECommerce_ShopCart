package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.AdminNotFoundException;
import com.example.demo.feign.OrderClient;
import com.example.demo.feign.ProductClient;
import com.example.demo.feign.ShoppingCartClient;
import com.example.demo.feign.UserClient;
import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

	Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Autowired
	private UserClient userClient;
	@Autowired
	private ProductClient productClient;
	@Autowired
	private OrderClient orderClient;
	@Autowired
	private ShoppingCartClient cartClient;
	@Autowired
	private AdminRepository repository;

	// Saves an admin entity to the repository.
    // Logs the operation and returns a success message.
	@Override
	public String saveAdmin(Admin admin) {
		log.info("In AdminServiceImpl saveAdmin method....");
		repository.save(admin);
		log.info("Admin saved with ID: {}", admin.getAdminId());
		return "Admin saved successfully.";
	}

	// Updates an existing admin entity in the repository.
    // Logs the update attempt and returns the updated admin object.
	@Override
	public Admin updateAdmin(Admin admin) {
		log.info("In AdminServiceImpl updateAdmin method....");
		Admin updatedAdmin = repository.save(admin);
		log.info("Admin updated with ID: {}", updatedAdmin.getAdminId());
		return updatedAdmin;
	}

	// Retrieves an admin by its ID from the repository.
    // Logs the retrieval attempt.
    // Throws an exception if the admin is not found.
	@Override
	public Admin getAdminById(int id) {
		log.info("Fetching admin by ID: {}", id);
		return repository.findById(id).orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + id));
	}

	// Retrieves all admin entities from the repository.
    // Logs the total number of admins fetched.
	@Override
	public List<Admin> getAllAdmins() {
		log.info("Fetching all admins...");
		List<Admin> admins = repository.findAll();
		log.info("Total admins fetched: {}", admins.size());
		return admins;
	}

	// Deletes an admin entity by its ID if it exists in the repository.
    // Logs the deletion attempt and throws an exception if the admin is not found.
	@Override
	public String deleteAdminById(int id) {
		log.info("Deleting admin with ID: {}", id);
		if (!repository.existsById(id)) {
			log.warn("Admin not found with ID: {}", id);
			throw new AdminNotFoundException("Admin not found with ID: " + id);
		}
		repository.deleteById(id);
		log.info("Admin deleted with ID: {}", id);
		return "Admin deleted successfully.";
	}

	// Retrieves all users from the user service via a Feign client.
    // Logs the total number of users fetched.
	@Override
	public List<UserDTO> fetchAllUsers() {
		log.info("Fetching all users...");
		List<UserDTO> users = userClient.getAllUsers();
		log.info("Total users fetched: {}", users.size());
		return users;
	}

	// Retrieves all products from the product service via a Feign client.
    // Logs the total number of products fetched.
	@Override
	public List<ProductDTO> fetchAllProducts() {
		log.info("Fetching all products...");
		List<ProductDTO> products = productClient.getAllProducts();
		log.info("Total products fetched: {}", products.size());
		return products;
	}

	// Retrieves all orders from the order service via a Feign client.
    // Logs the total number of orders fetched.
	@Override
	public List<OrderDTO> fetchAllOrders() {
		log.info("Fetching all orders...");
		List<OrderDTO> orders = orderClient.getAllOrders();
		log.info("Total orders fetched: {}", orders.size());
		return orders;
	}

	// Retrieves all cart items from the cart service via a Feign client.
    // Logs the total number of cart items fetched.
	@Override
	public List<CartItemDTO> fetchAllCartItems() {
		log.info("Fetching all cart items...");
		List<CartItemDTO> cartItems = cartClient.getAllCartItems();
		log.info("Total cart items fetched: {}", cartItems.size());
		return cartItems;
	}

	// Deletes a user entity via the user service.
    // Logs the response from the user service.
	@Override
	public String deleteUser(int id) {
		log.info("Deleting user with ID: {}", id);
		String response = userClient.deleteUser(id);
		log.info("Response from user service for deleting user: {}", response);
		return response;
	}

	// Deletes a product entity via the product service.
    // Logs the response from the product service.
	@Override
	public String deleteProduct(int id) {
		log.info("Deleting product with ID: {}", id);
		String response = productClient.deleteProduct(id);
		log.info("Response from product service for deleting product: {}", response);
		return response;
	}

	// Deletes an order entity via the order service.
    // Logs the response from the order service.
	@Override
	public String deleteOrder(int id) {
		log.info("Deleting order with ID: {}", id);
		String response = orderClient.deleteOrder(id);
		log.info("Response from order service for deleting order: {}", response);
		return response;
	}

	// Deletes a cart item via the cart service.
    // Logs the response from the cart service.
	@Override
	public String deleteCartItem(int id) {
		log.info("Deleting cart item with ID: {}", id);
		String response = cartClient.deleteCartItem(id);
		log.info("Response from cart service for deleting cart item: {}", response);
		return response;
	}

	@Override
	public Admin getAdminByName(String name) {
		log.info("In AdminServiceImpl getAdminByName method....");
		return repository.findByName(name)
				.orElseThrow(() -> new AdminNotFoundException("Admin with name '" + name + "' not found"));
	}
}