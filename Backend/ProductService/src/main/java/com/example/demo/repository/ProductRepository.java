package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByCategory(String category);
	
	List<Product> findByPriceBetween(double min, double max);
}