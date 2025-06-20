package com.example.demo.feign;

import com.example.demo.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "PRODUCTSERVICE")
public interface ProductClient {

	@GetMapping("/product/fetchById/{productId}")
	ProductDTO getProductById(@PathVariable("productId") int productId);
	
	@PutMapping("/product/decreaseQuantity/{productId}/{quantity}")
    String decreaseProductQuantity(@PathVariable("productId") int productId, @PathVariable("quantity") int quantity);
}