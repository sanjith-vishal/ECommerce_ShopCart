package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.example.demo.dto.CartItemWithProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.CartItem;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.service.CartItemServiceImpl;
import com.example.demo.feign.ProductClient;
import com.example.demo.feign.UserClient;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingCartServiceApplicationTests {

    @Mock
    private ProductClient productClient;

    @Mock
    private UserClient userClient;

    @Mock
    private CartItemRepository repository;

    @InjectMocks
    private CartItemServiceImpl service;

    @Test
    void addCartItemTest() {
        CartItem item = new CartItem(0, 1, 101, 2, 0);
        ProductDTO product = new ProductDTO(101, "TV", "Electronics", 25000, "", "", 10);

        when(productClient.getProductById(101)).thenReturn(product);
        when(repository.save(any(CartItem.class))).thenReturn(item);

        String result = service.addCartItem(item);
        assertEquals("Item added to cart successfully.", result);
        assertEquals(50000, item.getTotalPrice());
    }

    @Test
    void updateCartItemTest() {
        CartItem item = new CartItem(1, 1, 101, 3, 75000);
        when(repository.save(item)).thenReturn(item);

        CartItem updated = service.updateCartItem(item);
        assertEquals(3, updated.getQuantity());
    }

    @Test
    void getCartItemById_FoundTest() {
        CartItem item = new CartItem(1, 1, 101, 2, 50000);
        when(repository.findById(1)).thenReturn(Optional.of(item));

        CartItem result = service.getCartItemById(1);
        assertNotNull(result);
        assertEquals(101, result.getProductId());
    }

    @Test
    void getCartItemById_NotFoundTest() {
        when(repository.findById(2)).thenReturn(Optional.empty());

        CartItem result = service.getCartItemById(2);
        assertNull(result);
    }

    @Test
    void getAllCartItemsTest() {
        List<CartItem> list = Arrays.asList(
                new CartItem(1, 1, 101, 2, 50000),
                new CartItem(2, 2, 102, 1, 30000)
        );

        when(repository.findAll()).thenReturn(list);

        List<CartItem> result = service.getAllCartItems();
        assertEquals(2, result.size());
    }

    @Test
    void deleteCartItemById_FoundTest() {
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);

        String result = service.deleteCartItemById(1);
        assertEquals("Cart item deleted.", result);
    }

    @Test
    void deleteCartItemById_NotFoundTest() {
        when(repository.existsById(99)).thenReturn(false);

        String result = service.deleteCartItemById(99);
        assertEquals("Cart item not found.", result);
    }

    @Test
    void getCartItemsByUserIdTest() {
        List<CartItem> list = Arrays.asList(
                new CartItem(1, 10, 101, 1, 25000),
                new CartItem(2, 10, 102, 2, 60000)
        );

        when(repository.findByUserId(10)).thenReturn(list);

        List<CartItem> result = service.getCartItemsByUserId(10);
        assertEquals(2, result.size());
    }

    @Test
    void getCartItemWithProductTest() {
        CartItem item = new CartItem(1, 1, 101, 2, 50000);
        ProductDTO product = new ProductDTO(101, "TV", "Electronics", 25000, "", "", 10);

        when(repository.findById(1)).thenReturn(Optional.of(item));
        when(productClient.getProductById(101)).thenReturn(product);

        CartItemWithProductDTO dto = service.getCartItemWithProduct(1);

        assertNotNull(dto);
        assertEquals("TV", dto.getProduct().getProductName());
    }

    @Test
    void getCartItemsWithProductsByUserIdTest() {
        List<CartItem> items = Arrays.asList(
                new CartItem(1, 20, 101, 1, 25000),
                new CartItem(2, 20, 102, 2, 60000)
        );

        when(repository.findByUserId(20)).thenReturn(items);
        when(productClient.getProductById(101)).thenReturn(
                new ProductDTO(101, "TV", "Electronics", 25000, "", "", 10));
        when(productClient.getProductById(102)).thenReturn(
                new ProductDTO(102, "Fridge", "Electronics", 30000, "", "", 5));

        List<CartItemWithProductDTO> result = service.getCartItemsWithProductsByUserId(20);
        assertEquals(2, result.size());
        assertEquals("Fridge", result.get(1).getProduct().getProductName());
    }

    @Test
    void getCartItemsWithUserTest() {
        List<CartItem> items = Arrays.asList(
                new CartItem(1, 5, 101, 1, 25000)
        );

        UserDTO user = new UserDTO(5, "Alice", "alice@example.com","");

        when(repository.findByUserId(5)).thenReturn(items);
        when(userClient.getUserById(5)).thenReturn(user);

        var result = service.getCartItemsWithUser(5);
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getUser().getName());
    }
}