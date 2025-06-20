package com.example.demo.feign;

import com.example.demo.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USERSERVICE")
public interface UserClient {
	@GetMapping("/user/fetchById/{userId}")
	UserDTO getUser(@PathVariable("userId") int userId);
}
