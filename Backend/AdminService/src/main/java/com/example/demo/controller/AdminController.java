package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Admin;
import com.example.demo.service.AdminService;
import com.example.demo.exceptions.AdminNotFoundException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

	AdminService service;

	@PostMapping("/save")
	public String saveAdmin(@Valid @RequestBody Admin admin) {
		return service.saveAdmin(admin);
	}

	@PutMapping("/update")
	public Admin updateAdmin(@Valid @RequestBody Admin admin) {
		return service.updateAdmin(admin);
	}

	@GetMapping("/fetchById/{id}")
	public Admin getAdminById(@PathVariable int id) {
		return service.getAdminById(id);
	}

	@GetMapping("/fetchAll")
	public List<Admin> getAllAdmins() {
		return service.getAllAdmins();
	}

	@DeleteMapping("/delete/{id}")
	public String deleteAdmin(@PathVariable int id) {
		return service.deleteAdminById(id);
	}

	@GetMapping("/users")
	public Object getAllUsers() {
		return service.fetchAllUsers();
	}

	@GetMapping("/products")
	public Object getAllProducts() {
		return service.fetchAllProducts();
	}

	@GetMapping("/orders")
	public Object getAllOrders() {
		return service.fetchAllOrders();
	}

	@GetMapping("/carts")
	public Object getAllCarts() {
		return service.fetchAllCartItems();
	}

	@DeleteMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable int id) {
		return service.deleteUser(id);
	}

	@DeleteMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id) {
		return service.deleteProduct(id);
	}

	@DeleteMapping("/deleteOrder/{id}")
	public String deleteOrder(@PathVariable int id) {
		return service.deleteOrder(id);
	}

	@DeleteMapping("/deleteCart/{id}")
	public String deleteCartItem(@PathVariable int id) {
		return service.deleteCartItem(id);
	}
	
	@GetMapping("/fetchByName/{name}")
	public ResponseEntity<Admin> getAdminByName(@PathVariable("name") String name) {
		try {
			Admin admin = service.getAdminByName(name);
			return ResponseEntity.ok(admin); // Return 200 OK with the admin object
		} catch (AdminNotFoundException e) {
			// If admin not found, return 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}
}
