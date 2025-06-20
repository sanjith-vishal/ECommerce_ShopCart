package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Admin;

public interface AdminService {
	public abstract String saveAdmin(Admin admin);

	public abstract Admin updateAdmin(Admin admin);

	public abstract Admin getAdminById(int id);

	public abstract List<Admin> getAllAdmins();

	public abstract String deleteAdminById(int id);

	List<UserDTO> fetchAllUsers();

	List<ProductDTO> fetchAllProducts();

	List<OrderDTO> fetchAllOrders();

	List<CartItemDTO> fetchAllCartItems();

	String deleteUser(int id);

	String deleteProduct(int id);

	String deleteOrder(int id);

	String deleteCartItem(int id);
	
	public abstract Admin getAdminByName(String name);
}
