package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.CartItemWithProductDTO;
import com.example.demo.dto.OrderSummaryDTO;
import com.example.demo.dto.OrderWithProductDTO;
import com.example.demo.dto.OrderWithProductDetailsDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductInOrderDTO;
import com.example.demo.dto.ProductWithQuantityDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.OrderNotFoundException;
import com.example.demo.feign.ProductClient;
import com.example.demo.feign.ShoppingCartClient;
import com.example.demo.feign.UserClient;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

	Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private ShoppingCartClient cartClient;

	@Autowired
	private OrderRepository repository;

	@Autowired
	private ProductClient productClient;

	@Autowired
	private UserClient userClient;
	
	@Autowired
    private OrderItemRepository orderItemRepository; // Autowire the new repository

	// Places an order and saves it in the repository.
	// Logs the order placement and returns a confirmation message.
	@Override
	public String placeOrder(Order order) {
		log.info("In OrderServiceImpl placeOrder method....");
		repository.save(order);
		log.info("Order placed successfully with ID: {}", order.getOrderId());
		return "Order placed successfully.";
	}

	/**
     * Updates an existing order in the repository.
     * This method is designed to handle partial updates, typically for order status or shipping address.
     * It fetches the existing order from the database and only updates the provided fields.
     *
     * @param order The Order object containing the orderId and the fields to be updated.
     * @return The updated Order object.
     * @throws OrderNotFoundException if the order with the given ID does not exist.
     */
    @Override
    @Transactional // Ensure the update is atomic
    public Order updateOrder(Order order) {
        log.info("Attempting to update order with ID: {}", order.getOrderId());

        // 1. Fetch the existing order from the database
        Optional<Order> optionalExistingOrder = repository.findById(order.getOrderId());

        if (optionalExistingOrder.isEmpty()) {
            log.warn("Order not found with ID: {}", order.getOrderId());
            throw new OrderNotFoundException("Order not found with ID: " + order.getOrderId());
        }

        Order existingOrder = optionalExistingOrder.get();

        // 2. Apply updates only for the fields that are provided in the incoming 'order' object.
        //    Crucially, check if the incoming field is not null/blank before applying.
        //    This prevents overwriting existing data with nulls or empty strings from partial requests.

        // Update Order Status (Commonly updated by admin)
        if (order.getOrderStatus() != null && !order.getOrderStatus().isBlank()) {
            if (!existingOrder.getOrderStatus().equals(order.getOrderStatus())) { // Check if status actually changed
                existingOrder.setOrderStatus(order.getOrderStatus());
                log.debug("Order ID {}: Order status updated to '{}'", order.getOrderId(), order.getOrderStatus());
            }
        }

        // Update Payment Status (Also commonly updated by admin)
        if (order.getPaymentStatus() != null && !order.getPaymentStatus().isBlank()) {
            if (!existingOrder.getPaymentStatus().equals(order.getPaymentStatus())) { // Check if status actually changed
                existingOrder.setPaymentStatus(order.getPaymentStatus());
                log.debug("Order ID {}: Payment status updated to '{}'", order.getOrderId(), order.getPaymentStatus());
            }
        }

        // Update Shipping Address (If this is allowed to be changed by admin)
        if (order.getShippingAddress() != null && !order.getShippingAddress().isBlank()) {
            if (!existingOrder.getShippingAddress().equals(order.getShippingAddress())) { // Check if address actually changed
                existingOrder.setShippingAddress(order.getShippingAddress());
                log.debug("Order ID {}: Shipping address updated to '{}'", order.getOrderId(), order.getShippingAddress());
            }
        }

        // Do NOT update userId, totalPrice, orderDate, or orderItems directly from the incoming 'order'
        // object for an update operation. These fields are typically set during order creation
        // and should not be modified through a generic update endpoint. If they need to change,
        // it implies a different business process (e.g., refund, order cancellation/re-creation).

        // 3. Save the modified existing order.
        // Spring Data JPA will perform an update because the entity has an ID.
        // Since 'existingOrder' is now a complete and valid object, validation will pass.
        Order updatedOrder = repository.save(existingOrder);
        log.info("Order updated successfully with ID: {}", updatedOrder.getOrderId());
        return updatedOrder;
    }

	// Retrieves all orders from the repository.
	// Logs the total number of orders fetched.
    @Override
    public List<OrderWithProductDetailsDTO> getAllOrders() {
        log.info("Fetching all orders for admin with product details...");
        List<Order> orders = repository.findAll(); // Fetch all Order entities

        List<OrderWithProductDetailsDTO> result = new ArrayList<>();
        for (Order order : orders) {
            // Create the main DTO
            OrderWithProductDetailsDTO orderDto = new OrderWithProductDetailsDTO();
            orderDto.setOrderId(order.getOrderId());
            orderDto.setUserId(order.getUserId());
            orderDto.setTotalPrice(order.getTotalPrice());
            orderDto.setShippingAddress(order.getShippingAddress());
            orderDto.setOrderStatus(order.getOrderStatus());
            orderDto.setPaymentStatus(order.getPaymentStatus());
            orderDto.setOrderDate(order.getOrderDate()); // Assuming orderDate is set in Order entity

            // Map OrderItems to ProductInOrderDTOs
            List<ProductInOrderDTO> productItems = new ArrayList<>();
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    // Assuming OrderItem already contains productName, priceAtOrder, etc.
                    // If not, you might need to call productClient.getProductById(item.getProductId()) here
                    // to fetch product details. But if OrderItem holds enough, just map it.
                    productItems.add(new ProductInOrderDTO(
                        item.getProductId(),
                        item.getProductName(), // Assuming OrderItem has productName
                        item.getPriceAtOrder(), // Assuming OrderItem has priceAtOrder
                        item.getQuantity()
                    ));
                }
            }
            orderDto.setItems(productItems);
            result.add(orderDto);
        }
        log.info("Total orders with details fetched for admin: {}", result.size());
        return result;
    }

	// Deletes an order by its ID if it exists in the repository.
	// Logs the deletion attempt and throws an exception if the order is not found.
	@Override
	public String deleteOrderById(int orderId) {
		log.info("Attempting to delete order with ID: {}", orderId);
		if (repository.existsById(orderId)) {
			repository.deleteById(orderId);
			log.info("Order deleted with ID: {}", orderId);
			return "Order deleted.";
		} else {
			log.warn("Order not found with ID: {}", orderId);
			throw new OrderNotFoundException("Order not found with ID: " + orderId);
		}
	}

	// Retrieves orders filtered by order status.
	// Logs the attempt and number of orders found with the specified status.
	@Override
	public List<Order> getOrdersByOrderStatus(String status) {
		log.info("Fetching orders with status: {}", status);
		List<Order> orders = repository.findByOrderStatus(status);
		log.info("Total orders fetched with status {}: {}", status, orders.size());
		return orders;
	}

	// Retrieves orders filtered by payment status.
	// Logs the attempt and number of orders found with the specified payment
	// status.
	@Override
	public List<Order> getOrdersByPaymentStatus(String status) {
		log.info("Fetching orders with payment status: {}", status);
		List<Order> orders = repository.findByPaymentStatus(status);
		log.info("Total orders fetched with payment status {}: {}", status, orders.size());
		return orders;
	}

      
    @Override
    @Transactional
    public String placeOrderByUserId(int userId, Order orderDetails) {
        log.info("Placing order for user with ID: {}", userId);
        List<CartItemWithProductDTO> cartItemsWithProducts = cartClient.getCartItemsWithProducts(userId); // Get with product details

        if (cartItemsWithProducts.isEmpty()) {
            log.warn("Cart is empty for user ID: {}", userId);
            return "Cart is empty. Cannot place order.";
        }

        // --- Calculate total price and save order ---
        double totalOrderPrice = cartItemsWithProducts.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum(); // Calculate total from product price and quantity

        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setTotalPrice(totalOrderPrice);
        newOrder.setShippingAddress(orderDetails.getShippingAddress());
        newOrder.setOrderStatus(orderDetails.getOrderStatus());
        newOrder.setPaymentStatus(orderDetails.getPaymentStatus());
        newOrder.setOrderDate(LocalDateTime.now()); // Set the current order date

        // Save the order first to get its ID
        repository.save(newOrder);
        log.info("Order placed successfully for user ID: {}. Order ID: {}", userId, newOrder.getOrderId());

        // --- Create and save OrderItems ---
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemWithProductDTO cartItem : cartItemsWithProducts) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder); // Link to the newly created order
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProduct().getProductName());
            orderItem.setPriceAtOrder(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);

            // Decrease product quantities via Feign Client (this remains the same)
            try {
                log.info("Attempting to decrease quantity for product ID: {} by {}", cartItem.getProductId(), cartItem.getQuantity());
                productClient.decreaseProductQuantity(cartItem.getProductId(), cartItem.getQuantity());
                log.info("Successfully decreased quantity for product ID: {}", cartItem.getProductId());
            } catch (Exception e) {
                log.error("Failed to decrease quantity for product ID: {} during order placement: {}", cartItem.getProductId(), e.getMessage());
                throw new RuntimeException("Order failed due to insufficient stock for product ID: " + cartItem.getProductId() + ". " + e.getMessage());
            }
        }
        orderItemRepository.saveAll(orderItems); // Save all order items in a batch
        log.info("Saved {} order items for order ID: {}", orderItems.size(), newOrder.getOrderId());

        // --- Clear cart (remains the same) ---
        try {
            log.info("Attempting to clear cart for user ID: {} after successful order placement.", userId);
            cartClient.clearCartByUserId(userId);
            log.info("Cart cleared successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to clear cart for user ID: {} after order placement: {}", userId, e.getMessage());
        }
        return "Order placed successfully. Order ID: " + newOrder.getOrderId();
    }

    @Override
    public OrderWithProductDTO getOrderWithProductDetails(int orderId) {
        log.info("Fetching order with product details for Order ID: {}", orderId);
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        // It seems `getOrderWithProductDetails` should fetch products directly related to the order,
        // not necessarily from the current cart. This part of the logic might need review
        // if your Order model doesn't store individual product quantities.
        // Assuming for now, it's retrieving from the cart for display purposes post-order.
        List<CartItemWithProductDTO> cartItems = cartClient.getCartItemsWithProducts(order.getUserId());
        List<ProductWithQuantityDTO> products = new ArrayList<>();

        for (CartItemWithProductDTO item : cartItems) {
            ProductDTO product = item.getProduct();
            ProductWithQuantityDTO dto = new ProductWithQuantityDTO();

            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setQuantity(item.getQuantity());

            products.add(dto);
        }

        OrderWithProductDTO response = new OrderWithProductDTO();
        response.setOrder(order);
        response.setProducts(products);

        log.info("Fetched order with product details for Order ID: {}", orderId);
        return response;
    }

    @Override
    public List<OrderWithProductDetailsDTO> getOrdersByUserId(int userId) {
        log.info("Fetching orders with product details for user ID: {}", userId);
        List<Order> orders = repository.findByUserId(userId);

        if (orders.isEmpty()) {
            // No orders found, return an empty list rather than throwing an exception
            // The frontend handles an empty list by showing "No orders yet"
            log.warn("No orders found for user ID: {}", userId);
            return new ArrayList<>();
        }

        UserDTO user = userClient.getUser(userId); // Fetch user details once

        List<OrderWithProductDetailsDTO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderWithProductDetailsDTO dto = new OrderWithProductDetailsDTO();
            dto.setOrderId(order.getOrderId());
            dto.setUserId(order.getUserId());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setShippingAddress(order.getShippingAddress());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setOrderDate(order.getOrderDate()); // Set the actual order date
            dto.setUser(user);

            // Populate items from the OrderItem entity
            List<ProductInOrderDTO> orderProducts = new ArrayList<>();
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    orderProducts.add(new ProductInOrderDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPriceAtOrder(),
                        item.getQuantity()
                    ));
                }
            }
            dto.setItems(orderProducts);
            result.add(dto);
        }

        log.info("Fetched {} orders with product details for user ID: {}", result.size(), userId);
        return result;
    }
}