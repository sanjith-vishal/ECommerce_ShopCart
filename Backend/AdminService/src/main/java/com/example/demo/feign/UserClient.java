package com.example.demo.feign;

import com.example.demo.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "USERSERVICE")
public interface UserClient {
	@GetMapping("/user/fetchAll")
	List<UserDTO> getAllUsers();

	@DeleteMapping("/user/delete/{id}")
	String deleteUser(@PathVariable int id);
}