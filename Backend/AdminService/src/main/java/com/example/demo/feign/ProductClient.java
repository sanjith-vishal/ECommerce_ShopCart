package com.example.demo.feign;

import com.example.demo.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "PRODUCTSERVICE")
public interface ProductClient {
	@GetMapping("/product/fetchAll")
	List<ProductDTO> getAllProducts();

	@DeleteMapping("/product/delete/{id}")
	String deleteProduct(@PathVariable int id);
}
