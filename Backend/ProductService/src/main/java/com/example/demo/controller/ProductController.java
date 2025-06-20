package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exceptions.InsufficientStockException;
import com.example.demo.exceptions.ProductNotFound;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	ProductService service;

	@PostMapping("/save")
	public String saveProduct(@Valid @RequestBody Product product) {
		return service.saveProduct(product);
	}

	@PutMapping("/update")
	public Product updateProduct(@Valid @RequestBody Product product) {
		return service.updateProduct(product);
	}

	@GetMapping("/fetchById/{id}")
	public Product getProduct(@PathVariable("id") int productId) {
		return service.getProduct(productId);
	}

	@GetMapping("/fetchAll")
	public List<Product> getAllProducts() {
		return service.getAllProducts();
	}

	@DeleteMapping("/delete/{id}")
	public String deleteProduct(@PathVariable("id") int productId) {
		return service.deleteProduct(productId);
	}

	@GetMapping("/category/{category}")
	public List<Product> getByCategory(@PathVariable String category) {
		return service.getProductsByCategory(category);
	}

	@GetMapping("/price-range/{start}/{end}")
	public List<Product> getByPriceRange(@PathVariable("start") double min, @PathVariable("end") double max) {
		return service.getProductsByPriceRange(min, max);
	}

	@PutMapping("/decreaseQuantity/{productId}/{quantity}")
    public ResponseEntity<String> decreaseProductQuantity(@PathVariable("productId") int productId, @PathVariable("quantity") int quantity) {
		service.updateProductQuantity(productId, -quantity); // Pass negative quantity for decrease
		return ResponseEntity.ok("Product quantity decreased successfully for product ID: " + productId);
    }
}