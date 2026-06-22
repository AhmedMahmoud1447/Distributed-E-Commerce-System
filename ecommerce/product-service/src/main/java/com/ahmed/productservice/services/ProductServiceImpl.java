package com.ahmed.productservice.services;


import com.ahmed.productservice.domain.Category;
import com.ahmed.productservice.domain.Product;
import com.ahmed.productservice.dtos.ProductDto;
import com.ahmed.productservice.error.exceptions.ResourceAlreadyExistsException;
import com.ahmed.productservice.error.exceptions.ResourceNotFoundException;
import com.ahmed.productservice.mappers.ProductMapper;
import com.ahmed.productservice.repositories.CategoryRepository;
import com.ahmed.productservice.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto.Response createProduct(ProductDto.Request request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new ResourceAlreadyExistsException("Product with SKU " + request.sku() + " already exists!");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductDto.Response getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Override
    public Page<ProductDto.Response> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductDto.Response updateProduct(Long id, ProductDto.Request request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (!existingProduct.getSku().equals(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new ResourceAlreadyExistsException("SKU " + request.sku() + " is already taken by another product!");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setPrice(request.price());
        existingProduct.setSku(request.sku());
        existingProduct.setImageUrl(request.imageUrl());
        existingProduct.setCategory(category);

        return productMapper.toResponse(productRepository.save(existingProduct));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}