package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.example.demo.dto.*;
import com.example.demo.feign.*;
import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import com.example.demo.service.AdminServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminServiceApplicationTests {

    @Mock
    private AdminRepository repository;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderClient orderClient;

    @Mock
    private ShoppingCartClient cartClient;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin admin;

    @Test
    void testSaveAdmin() {
        when(repository.save(admin)).thenReturn(admin);
        String result = adminService.saveAdmin(admin);
        assertEquals("Admin saved successfully.", result);
    }

    @Test
    void testUpdateAdmin() {
        when(repository.save(admin)).thenReturn(admin);
        Admin result = adminService.updateAdmin(admin);
        assertEquals(admin, result);
    }

    @Test
    void testGetAdminById_Found() {
        when(repository.findById(1)).thenReturn(Optional.of(admin));
        Admin result = adminService.getAdminById(1);
        assertEquals(admin, result);
    }

    @Test
    void testGetAdminById_NotFound() {
        when(repository.findById(2)).thenReturn(Optional.empty());
        Admin result = adminService.getAdminById(2);
        assertNull(result);
    }

    @Test
    void testGetAllAdmins() {
        List<Admin> list = List.of(admin);
        when(repository.findAll()).thenReturn(list);
        List<Admin> result = adminService.getAllAdmins();
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteAdminById_Found() {
        when(repository.existsById(1)).thenReturn(true);
        String result = adminService.deleteAdminById(1);
        verify(repository).deleteById(1);
        assertEquals("Admin deleted successfully.", result);
    }

    @Test
    void testDeleteAdminById_NotFound() {
        when(repository.existsById(2)).thenReturn(false);
        String result = adminService.deleteAdminById(2);
        assertEquals("Admin not found.", result);
    }

    @Test
    void testFetchAllUsers() {
        List<UserDTO> mockUsers = List.of(new UserDTO(1, "Alice", "a@mail.com", "Addr"));
        when(userClient.getAllUsers()).thenReturn(mockUsers);
        List<UserDTO> result = adminService.fetchAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testFetchAllProducts() {
        List<ProductDTO> mockProducts = List.of(new ProductDTO(1, "Item", "Desc", 100, "Cat", 5));
        when(productClient.getAllProducts()).thenReturn(mockProducts);
        List<ProductDTO> result = adminService.fetchAllProducts();
        assertEquals(1, result.size());
    }

    @Test
    void testFetchAllOrders() {
        List<OrderDTO> mockOrders = List.of(new OrderDTO(1, 1, 200, "Addr", "Shipped", "Paid"));
        when(orderClient.getAllOrders()).thenReturn(mockOrders);
        List<OrderDTO> result = adminService.fetchAllOrders();
        assertEquals(1, result.size());
    }

    @Test
    void testFetchAllCartItems() {
        List<CartItemDTO> mockItems = List.of(new CartItemDTO(1, 1, 1, 2, 199.98));
        when(cartClient.getAllCartItems()).thenReturn(mockItems);
        List<CartItemDTO> result = adminService.fetchAllCartItems();
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteUser() {
        when(userClient.deleteUser(1)).thenReturn("User Deleted");
        String result = adminService.deleteUser(1);
        assertEquals("User Deleted", result);
    }

    @Test
    void testDeleteProduct() {
        when(productClient.deleteProduct(1)).thenReturn("Product Deleted");
        String result = adminService.deleteProduct(1);
        assertEquals("Product Deleted", result);
    }

    @Test
    void testDeleteOrder() {
        when(orderClient.deleteOrder(1)).thenReturn("Order Deleted");
        String result = adminService.deleteOrder(1);
        assertEquals("Order Deleted", result);
    }

    @Test
    void testDeleteCartItem() {
        when(cartClient.deleteCartItem(1)).thenReturn("Cart Item Deleted");
        String result = adminService.deleteCartItem(1);
        assertEquals("Cart Item Deleted", result);
    }
}