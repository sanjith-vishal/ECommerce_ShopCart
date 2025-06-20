package com.example.demo.service;

import java.util.List;
import com.example.demo.model.User;

public interface UserService {
	public abstract String saveUser(User user);

	public abstract User updateUser(User user);

	public abstract User getUserById(int userId);

	public abstract List<User> getAllUsers();

	public abstract String deleteUserById(int userId);
	
	public abstract User getUserByName(String name);
}