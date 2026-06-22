package com.ahmed.productservice.services;


import com.ahmed.productservice.domain.Category;
import com.ahmed.productservice.domain.Product;
import com.ahmed.productservice.dtos.CategoryDto;
import com.ahmed.productservice.dtos.ProductDto;
import com.ahmed.productservice.error.exceptions.ResourceAlreadyExistsException;
import com.ahmed.productservice.error.exceptions.ResourceNotFoundException;
import com.ahmed.productservice.mappers.ProductMapper;
import com.ahmed.productservice.repositories.CategoryRepository;
import com.ahmed.productservice.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto.Request productRequest;
    private Product product;
    private Category category;
    private ProductDto.Response productResponse;

    @BeforeEach
    void setUp() {

        CategoryDto.Response mockCategoryResponse = new CategoryDto.Response(
                1L, "Electronics", "Gadgets and devices"
        );

        productRequest = new ProductDto.Request(
                "iPhone 15", "Apple smartphone", new BigDecimal("999.99"), "PROD-SKU-123",
                "http://image.com", 1L
        );

        category = new Category();
        product = new Product();

        productResponse = new ProductDto.Response(
                1L,
                "iPhone 15",
                "Apple smartphone",
                new BigDecimal("999.99"),
                "PROD-SKU-123",
                "http://image.com",
                mockCategoryResponse
        );
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should successfully create a product when SKU is unique and category exists")
        void shouldCreateProductSuccessfully() {
            // Given
            when(productRepository.existsBySku(productRequest.sku())).thenReturn(false);
            when(categoryRepository.findById(productRequest.categoryId())).thenReturn(Optional.of(category));
            when(productMapper.toEntity(productRequest)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            // When
            ProductDto.Response result = productService.createProduct(productRequest);

            // Then
            assertNotNull(result);
            assertEquals(productResponse.id(), result.id());
            assertEquals(productResponse.name(), result.name());
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw ResourceAlreadyExistsException when SKU already exists")
        void shouldThrowExceptionWhenSkuExists() {
            // Given
            when(productRepository.existsBySku(productRequest.sku())).thenReturn(true);

            // When & Then
            assertThrows(ResourceAlreadyExistsException.class, () -> productService.createProduct(productRequest));
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Product Tests")
    class GetProductTests {

        @Test
        @DisplayName("Should return product response when product exists by ID")
        void shouldReturnProductWhenIdExists() {
            // Given
            long productId = 1L;
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            // When
            ProductDto.Response result = productService.getProductById(productId);

            // Then
            assertNotNull(result);
            assertEquals(productId, result.id());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product does not exist by ID")
        void shouldThrowNotFoundException() {
            // Given
            long productId = 1L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should successfully delete product when it exists")
        void shouldDeleteProduct() {
            // Given
            long productId = 1L;
            when(productRepository.existsById(productId)).thenReturn(true);

            // When
            assertDoesNotThrow(() -> productService.deleteProduct(productId));

            // Then
            verify(productRepository, times(1)).deleteById(productId);
        }
    }
}