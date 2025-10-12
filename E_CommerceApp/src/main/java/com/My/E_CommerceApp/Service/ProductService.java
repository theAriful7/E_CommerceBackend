package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.ProductRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ProductResponseDTO;
import com.My.E_CommerceApp.Entity.Category;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Repository.AddressRepo;
import com.My.E_CommerceApp.Repository.CategoryRepo;
import com.My.E_CommerceApp.Repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public ProductService(ProductRepo productRepo, CategoryRepo categoryRepo) {

        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public Product toEntity(ProductRequestDTO dto, Category category) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        return product;
    }

    public ProductResponseDTO toDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        return dto;
    }

    // ✅ Create Product
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = toEntity(dto, category);
        Product saved = productRepo.save(product);
        return toDto(saved);
    }

    // ✅ Get Product by ID
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return toDto(product);
    }

    // ✅ Get All Products
    public List<ProductResponseDTO> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Update Product
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product existing = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);

        Product updated = productRepo.save(existing);
        return toDto(updated);
    }

    // ✅ Delete Product
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        productRepo.delete(product);
    }
}
