package com.example.demo.feign;

import com.example.demo.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCTSERVICE")
public interface ProductClient {

	@GetMapping("/product/fetchById/{id}")
	ProductDTO getProductById(@PathVariable("id") int id);
}
