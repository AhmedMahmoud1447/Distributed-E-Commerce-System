package com.ahmed.productservice.services;

import com.ahmed.productservice.dtos.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDto.Response createProduct(ProductDto.Request request);
    ProductDto.Response getProductById(Long id);
    Page<ProductDto.Response> getAllProducts(Pageable pageable);
    ProductDto.Response updateProduct(Long id, ProductDto.Request request);
    void deleteProduct(Long id);
}