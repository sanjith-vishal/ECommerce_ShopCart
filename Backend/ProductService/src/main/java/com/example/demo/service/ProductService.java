package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Product;

public interface ProductService {

	public abstract String saveProduct(Product product);

	public abstract Product updateProduct(Product product);

	public abstract Product getProduct(int productId);

	public abstract List<Product> getAllProducts();

	public abstract String deleteProduct(int productId);

	public abstract List<Product> getProductsByCategory(String category);

	public abstract List<Product> getProductsByPriceRange(double minPrice, double maxPrice);
	
	public abstract Product updateProductQuantity(int productId, int quantityChange);
}