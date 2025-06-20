package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.UserDTO;

@FeignClient(name = "USERSERVICE")
public interface UserClient {
	@GetMapping("/user/fetchById/{userId}")
	UserDTO getUserById(@PathVariable("userId") int userId);
}