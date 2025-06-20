package com.example.demo;

import com.example.demo.dto.*;
import com.example.demo.feign.ProductClient;
import com.example.demo.feign.ShoppingCartClient;
import com.example.demo.feign.UserClient;
import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceApplicationTests {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShoppingCartClient cartClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private UserClient userClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testPlaceOrder() {
//        Order order = new Order(0, 1, 500.0, "123 Street", "PENDING", "UNPAID");
//        when(orderRepository.save(order)).thenReturn(order);
//
//        String result = orderService.placeOrder(order);
//
//        assertEquals("Order placed successfully.", result);
//        verify(orderRepository, times(1)).save(order);
//    }

//    @Test
//    void testUpdateOrder() {
//        Order order = new Order(1, 1, 250.0, "New Address", "SHIPPED", "PAID");
//        when(orderRepository.save(order)).thenReturn(order);
//
//        Order updated = orderService.updateOrder(order);
//
//        assertEquals(order, updated);
//        verify(orderRepository).save(order);
//    }

//    @Test
//    void testGetAllOrders() {
//        List<Order> orders = List.of(new Order(), new Order());
//        when(orderRepository.findAll()).thenReturn(orders);
//
//        List<Order> result = orderService.getAllOrders();
//
//        assertEquals(2, result.size());
//        verify(orderRepository).findAll();
//    }

    @Test
    void testDeleteOrderById_Exists() {
        when(orderRepository.existsById(1)).thenReturn(true);

        String result = orderService.deleteOrderById(1);

        assertEquals("Order deleted.", result);
        verify(orderRepository).deleteById(1);
    }

    @Test
    void testDeleteOrderById_NotExists() {
        when(orderRepository.existsById(2)).thenReturn(false);

        String result = orderService.deleteOrderById(2);

        assertEquals("Order not found.", result);
        verify(orderRepository, never()).deleteById(anyInt());
    }

    @Test
    void testPlaceOrderByUserId_WithItems() {
        int userId = 10;

        List<CartItemDTO> cartItems = List.of(
                new CartItemDTO(1, userId, 101, 2, 100.0),
                new CartItemDTO(2, userId, 102, 1, 50.0)
        );

        Order inputOrder = new Order();
        inputOrder.setShippingAddress("Test Address");
        inputOrder.setOrderStatus("PLACED");
        inputOrder.setPaymentStatus("UNPAID");

        when(cartClient.getCartItemsByUserId(userId)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setOrderId(123);
            return o;
        });

        String result = orderService.placeOrderByUserId(userId, inputOrder);

        assertTrue(result.contains("Order placed successfully"));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testPlaceOrderByUserId_EmptyCart() {
        int userId = 10;
        when(cartClient.getCartItemsByUserId(userId)).thenReturn(Collections.emptyList());

        String result = orderService.placeOrderByUserId(userId, new Order());

        assertEquals("Cart is empty. Cannot place order.", result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrdersByOrderStatus() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findByOrderStatus("SHIPPED")).thenReturn(orders);

        List<Order> result = orderService.getOrdersByOrderStatus("SHIPPED");

        assertEquals(2, result.size());
        verify(orderRepository).findByOrderStatus("SHIPPED");
    }

    @Test
    void testGetOrdersByPaymentStatus() {
        List<Order> orders = List.of(new Order());
        when(orderRepository.findByPaymentStatus("PAID")).thenReturn(orders);

        List<Order> result = orderService.getOrdersByPaymentStatus("PAID");

        assertEquals(1, result.size());
        verify(orderRepository).findByPaymentStatus("PAID");
    }
}