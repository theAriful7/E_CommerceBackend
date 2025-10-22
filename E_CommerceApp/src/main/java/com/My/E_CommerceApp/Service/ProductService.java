package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.ProductRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.ProductSpecificationDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ProductResponseDTO;
import com.My.E_CommerceApp.Entity.*;
import com.My.E_CommerceApp.Enum.ProductStatus;
import com.My.E_CommerceApp.Exception.CustomException.OperationFailedException;
import com.My.E_CommerceApp.Exception.CustomException.ResourceNotFoundException;
import com.My.E_CommerceApp.Exception.CustomException.UnauthorizedAccessException;
import com.My.E_CommerceApp.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final ProductSpecificationRepo specificationRepo;
    private final SubCategoryRepo subCategoryRepo;

    // -------------------- Mapper: DTO to Entity -------------------- //
    public Product toEntity(ProductRequestDTO dto, Category category, User vendor) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrls(dto.getImageUrls() != null ? dto.getImageUrls() : new ArrayList<>());
        product.setCategory(category);
        product.setVendor(vendor);
        product.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : 0.0);
        product.setBrand(dto.getBrand());
        product.setStatus(ProductStatus.PENDING);

        // Handle specifications
        if (dto.getSpecifications() != null && !dto.getSpecifications().isEmpty()) {
            for (ProductSpecificationDTO specDTO : dto.getSpecifications()) {
                ProductSpecification specification = new ProductSpecification();
                specification.setKey(specDTO.getKey());
                specification.setValue(specDTO.getValue());
                specification.setDisplayOrder(specDTO.getDisplayOrder() != null ? specDTO.getDisplayOrder() : 0);
                specification.setProduct(product);
                product.getSpecifications().add(specification);
            }
        }

        // Handle sub-category if provided
        if (dto.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", dto.getSubCategoryId()));

            // Verify sub-category belongs to the selected category
            if (!subCategory.getCategory().getId().equals(category.getId())) {
                throw new OperationFailedException(
                        "Create product",
                        "Sub-category does not belong to the selected category"
                );
            }

            product.setSubCategory(subCategory);
        }

        return product;
    }

    // -------------------- Mapper: Entity to DTO -------------------- //
    public ProductResponseDTO toDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrls(product.getImageUrls());
        dto.setDiscount(product.getDiscount());
        dto.setBrand(product.getBrand());
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setStatus(product.getStatus());
        dto.setVendorId(product.getVendor() != null ? product.getVendor().getId() : null);
        dto.setVendorName(product.getVendor() != null ? product.getVendor().getFullName() : null);
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Handle specifications in response
        if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
            List<ProductSpecificationDTO> specDTOs = product.getSpecifications().stream()
                    .map(spec -> {
                        ProductSpecificationDTO specDTO = new ProductSpecificationDTO();
                        specDTO.setKey(spec.getKey());
                        specDTO.setValue(spec.getValue());
                        specDTO.setDisplayOrder(spec.getDisplayOrder());
                        return specDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSpecifications(specDTOs);
        } else {
            dto.setSpecifications(new ArrayList<>());
        }

        // Add sub-category name
        dto.setSubCategoryName(product.getSubCategory() != null ? product.getSubCategory().getName() : null);

        return dto;
    }

    // -------------------- Create Product -------------------- //
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto, Long vendorId) {
        try {
            // Find category
            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

            // Find vendor (user)
            User vendor = userRepo.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", vendorId));

            // Convert DTO to Entity
            Product product = toEntity(dto, category, vendor);

            // Save product
            Product savedProduct = productRepo.save(product);
            return toDto(savedProduct);
        } catch (Exception ex) {
            throw new OperationFailedException("Create product", ex.getMessage());
        }
    }

    // -------------------- Get Product By ID -------------------- //
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toDto(product);
    }

    // -------------------- Get All Products -------------------- //
    public List<ProductResponseDTO> getAllProducts() {
        try {
            List<Product> products = productRepo.findAll();
            return products.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve all products", ex.getMessage());
        }
    }

    // -------------------- Update Product -------------------- //
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto, Long vendorId) {
        try {
            // Find existing product
            Product existing = productRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            // Check if vendor owns this product
            if (!existing.getVendor().getId().equals(vendorId)) {
                throw new UnauthorizedAccessException("update this product");
            }

            // Find category
            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

            // Update basic fields
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setPrice(dto.getPrice());
            existing.setStock(dto.getStock());
            existing.setImageUrls(dto.getImageUrls() != null ? dto.getImageUrls() : existing.getImageUrls());
            existing.setCategory(category);
            existing.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : existing.getDiscount());
            existing.setBrand(dto.getBrand() != null ? dto.getBrand() : existing.getBrand());

            // Update sub-category if provided
            if (dto.getSubCategoryId() != null) {
                SubCategory subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", dto.getSubCategoryId()));

                // Verify sub-category belongs to the selected category
                if (!subCategory.getCategory().getId().equals(category.getId())) {
                    throw new OperationFailedException(
                            "Update product",
                            "Sub-category does not belong to the selected category"
                    );
                }

                existing.setSubCategory(subCategory);
            } else {
                existing.setSubCategory(null);
            }

            // Update specifications
            specificationRepo.deleteByProductId(id);
            existing.getSpecifications().clear();

            if (dto.getSpecifications() != null && !dto.getSpecifications().isEmpty()) {
                for (ProductSpecificationDTO specDTO : dto.getSpecifications()) {
                    ProductSpecification specification = new ProductSpecification();
                    specification.setKey(specDTO.getKey());
                    specification.setValue(specDTO.getValue());
                    specification.setDisplayOrder(specDTO.getDisplayOrder() != null ? specDTO.getDisplayOrder() : 0);
                    specification.setProduct(existing);
                    existing.getSpecifications().add(specification);
                }
            }

            Product updated = productRepo.save(existing);
            return toDto(updated);
        } catch (Exception ex) {
            throw new OperationFailedException("Update product", ex.getMessage());
        }
    }

    // -------------------- Delete Product -------------------- //
    @Transactional
    public void deleteProduct(Long id, Long vendorId) {
        try {
            Product product = productRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            // Check if vendor owns this product
            if (!product.getVendor().getId().equals(vendorId)) {
                throw new UnauthorizedAccessException("delete this product");
            }

            productRepo.delete(product);
        } catch (Exception ex) {
            throw new OperationFailedException("Delete product", ex.getMessage());
        }
    }

    // -------------------- Get Products By Vendor -------------------- //
    public List<ProductResponseDTO> getProductsByVendor(Long vendorId) {
        try {
            List<Product> vendorProducts = productRepo.findByVendorId(vendorId);
            return vendorProducts.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve vendor products", ex.getMessage());
        }
    }

    // -------------------- Get Products By Category -------------------- //
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        try {
            List<Product> categoryProducts = productRepo.findByCategoryId(categoryId);
            return categoryProducts.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve category products", ex.getMessage());
        }
    }

    // -------------------- Get Products By Status -------------------- //
    public List<ProductResponseDTO> getProductsByStatus(ProductStatus status) {
        try {
            List<Product> statusProducts = productRepo.findByStatus(status);
            return statusProducts.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve products by status", ex.getMessage());
        }
    }

    // -------------------- Change Product Status (Admin) -------------------- //
    @Transactional
    public ProductResponseDTO changeProductStatus(Long id, ProductStatus newStatus) {
        try {
            Product product = productRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            product.setStatus(newStatus);
            Product updated = productRepo.save(product);
            return toDto(updated);
        } catch (Exception ex) {
            throw new OperationFailedException("Change product status", ex.getMessage());
        }
    }

    // -------------------- Get Products By SubCategory -------------------- //
    public List<ProductResponseDTO> getProductsBySubCategory(Long subCategoryId) {
        try {
            // Verify sub-category exists
            if (!subCategoryRepo.existsById(subCategoryId)) {
                throw new ResourceNotFoundException("SubCategory", "id", subCategoryId);
            }

            List<Product> allProducts = productRepo.findAll();
            return allProducts.stream()
                    .filter(product -> product.getSubCategory() != null &&
                            product.getSubCategory().getId().equals(subCategoryId))
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve sub-category products", ex.getMessage());
        }
    }
}
