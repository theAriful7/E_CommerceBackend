package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.ProductRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ProductResponseDTO;
import com.My.E_CommerceApp.Entity.Category;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Enum.ProductStatus;
import com.My.E_CommerceApp.Repository.AddressRepo;
import com.My.E_CommerceApp.Repository.CategoryRepo;
import com.My.E_CommerceApp.Repository.ProductRepo;
import com.My.E_CommerceApp.Repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;

    // -------------------- Mapper -------------------- //
    public Product toEntity(ProductRequestDTO dto, Category category, User vendor) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        product.setVendor(vendor);
        product.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : 0.0);
        product.setBrand(dto.getBrand());
        product.setStatus(ProductStatus.PENDING); // default when created
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
        dto.setDiscount(product.getDiscount());
        dto.setBrand(product.getBrand());
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setStatus(product.getStatus());
        dto.setVendorId(product.getVendor() != null ? product.getVendor().getId() : null);
        dto.setVendorName(product.getVendor() != null ? product.getVendor().getFullName() : null);
        return dto;
    }

    // -------------------- Create Product -------------------- //
    public ProductResponseDTO createProduct(ProductRequestDTO dto, Long vendorId) {
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        User vendor = userRepo.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        Product product = toEntity(dto, category, vendor);
        Product saved = productRepo.save(product);
        return toDto(saved);
    }

    // -------------------- Get Product -------------------- //
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
        return toDto(product);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // -------------------- Update Product -------------------- //
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto, Long vendorId) {
        Product existing = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

        // Only the vendor who owns this product can update it, or admin (you can check role later)
        if (!existing.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);
        existing.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : existing.getDiscount());
        existing.setBrand(dto.getBrand() != null ? dto.getBrand() : existing.getBrand());

        Product updated = productRepo.save(existing);
        return toDto(updated);
    }

    // -------------------- Delete Product -------------------- //
    public void deleteProduct(Long id, Long vendorId) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

        // Only vendor or admin can delete
        if (!product.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("You are not allowed to delete this product");
        }

        productRepo.delete(product);
    }

    // -------------------- Get Products By Vendor -------------------- //
    public List<ProductResponseDTO> getProductsByVendor(Long vendorId) {
        return productRepo.findAll().stream()
                .filter(p -> p.getVendor() != null && p.getVendor().getId().equals(vendorId))
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
