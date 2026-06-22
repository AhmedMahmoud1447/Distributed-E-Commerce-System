package com.ahmed.productservice.controllers;

import com.ahmed.productservice.domain.Category;
import com.ahmed.productservice.dtos.ProductDto;
import com.ahmed.productservice.repositories.CategoryRepository;
import com.ahmed.productservice.repositories.ProductRepository;
import com.ahmed.productservice.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Controller Integration Tests (Full Flow)")
class ProductControllerIT extends AbstractBaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long sharedCategoryId;

    @BeforeEach
    void cleanUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Gadgets");

        Category savedCategory = categoryRepository.save(electronics);
        this.sharedCategoryId = savedCategory.getId();
    }

    @Test
    @DisplayName("Should create a product and retrieve it from database/cache successfully")
    void createAndGetProductIntegrationFlow() {

        ProductDto.Request request = new ProductDto.Request(
                "PlayStation 5",
                "Sony Console",
                new BigDecimal("499.99"),
                "PS5-SKU-999",
                "http://ps5.com/img.png",
                sharedCategoryId
        );

        ResponseEntity<ProductDto.Response> postResponse = restTemplate.postForEntity("/api/v1/products", request, ProductDto.Response.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        Long generatedId = postResponse.getBody().id();
        assertEquals("PlayStation 5", postResponse.getBody().name());

        ResponseEntity<ProductDto.Response> getResponse = restTemplate.getForEntity("/api/v1/products/" + generatedId, ProductDto.Response.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("PS5-SKU-999", getResponse.getBody().sku());
    }
}