package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceApplicationTests {

    @Mock
    ProductRepository repository;

    @InjectMocks
    ProductServiceImpl service;

    @Test
    void saveProductTest_Success() {
        Product product = new Product(1, "TV", "Smart TV", 50000.0, "Electronics", "Sony", 10);
        Mockito.when(repository.save(product)).thenReturn(product);

        String result = service.saveProduct(product);
        assertEquals("Product Saved !!!", result);
    }

    @Test
    void saveProductTest_Failure() {
        Product product = new Product(1, "TV", "Smart TV", 50000.0, "Electronics", "Sony", 10);
        Mockito.when(repository.save(product)).thenReturn(null);

        String result = service.saveProduct(product);
        assertEquals("Product Not Saved ...", result);
    }

    @Test
    void updateProductTest() {
        Product product = new Product(2, "AC", "Split AC", 35000.0, "Electronics", "LG", 5);
        Mockito.when(repository.save(product)).thenReturn(product);

        Product result = service.updateProduct(product);
        assertEquals("AC", result.getProductName());
    }

    @Test
    void getProductTest_Found() {
        Product product = new Product(3, "Fridge", "Double Door", 40000.0, "Electronics", "Samsung", 7);
        Mockito.when(repository.findById(3)).thenReturn(Optional.of(product));

        Product result = service.getProduct(3);
        assertEquals("Fridge", result.getProductName());
    }

    @Test
    void getProductTest_NotFound() {
        Mockito.when(repository.findById(5)).thenReturn(Optional.empty());

        Product result = service.getProduct(5);
        assertNull(result);
    }

    @Test
    void getAllProductsTest() {
        List<Product> list = Arrays.asList(
            new Product(1, "TV", "Smart TV", 50000.0, "Electronics", "Sony", 10),
            new Product(2, "AC", "Split AC", 35000.0, "Electronics", "LG", 5)
        );

        Mockito.when(repository.findAll()).thenReturn(list);

        List<Product> result = service.getAllProducts();
        assertEquals(2, result.size());
    }

    @Test
    void deleteProductTest_Found() {
        Product product = new Product(1, "TV", "Smart TV", 50000.0, "Electronics", "Sony", 10);
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(repository).deleteById(1);

        String result = service.deleteProduct(1);
        assertEquals("Product Deleted Successfully!", result);
    }

    @Test
    void deleteProductTest_NotFound() {
        Mockito.when(repository.findById(99)).thenReturn(Optional.empty());

        String result = service.deleteProduct(99);
        assertEquals("Product Not Found!", result);
    }

    @Test
    void getProductsByCategoryTest() {
        List<Product> list = Arrays.asList(
            new Product(1, "TV", "Smart TV", 50000.0, "Electronics", "Sony", 10),
            new Product(2, "AC", "Split AC", 35000.0, "Electronics", "LG", 5)
        );

        Mockito.when(repository.findByCategory("Electronics")).thenReturn(list);

        List<Product> result = service.getProductsByCategory("Electronics");
        assertEquals(2, result.size());
    }

    @Test
    void getProductsByPriceRangeTest() {
        List<Product> list = Arrays.asList(
            new Product(1, "TV", "Smart TV", 50000.0, "Electronics", "Sony", 10),
            new Product(2, "AC", "Split AC", 35000.0, "Electronics", "LG", 5)
        );

        Mockito.when(repository.findByPriceBetween(30000, 60000)).thenReturn(list);

        List<Product> result = service.getProductsByPriceRange(30000, 60000);
        assertEquals(2, result.size());
    }
}