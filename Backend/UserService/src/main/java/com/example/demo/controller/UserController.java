package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.exceptions.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService service;

	@PostMapping("/save")
	public String saveUser(@Valid @RequestBody User user) {
		return service.saveUser(user);
	}

	@PutMapping("/update")
	public User updateUser(@Valid @RequestBody User user) {
		return service.updateUser(user);
	}

	@GetMapping("/fetchById/{userId}")
	public User getUser(@PathVariable int userId) {
		return service.getUserById(userId);
	}

	@GetMapping("/fetchAll")
	public List<User> getAllUsers() {
		return service.getAllUsers();
	}

	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") int userId) {
		return service.deleteUserById(userId);
	}
	
	@GetMapping("/fetchByName/{name}")
	public ResponseEntity<User> getUserByName(@PathVariable("name") String name) {
		try {
			User user = service.getUserByName(name);
			return ResponseEntity.ok(user); // Return 200 OK with the user object
		} catch (UserNotFoundException e) {
			// If user not found, return 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}
	
	
}