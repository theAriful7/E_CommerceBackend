package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.ProductRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ProductResponseDTO;
import com.My.E_CommerceApp.Enum.ProductStatus;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ✅ Create Product - HTTP 201
    @PostMapping("/vendor/{vendorId}")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @PathVariable Long vendorId,
            @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto, vendorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Get All Products - HTTP 200
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }

    // ✅ Get Product By ID - HTTP 200
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // ✅ Get Products By Vendor - HTTP 200
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByVendor(@PathVariable Long vendorId) {
        List<ProductResponseDTO> response = productService.getProductsByVendor(vendorId);
        return ResponseEntity.ok(response);
    }

    // ✅ Get Products By Category - HTTP 200
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDTO> response = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    // ✅ Get Products By Status - HTTP 200
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByStatus(@PathVariable ProductStatus status) {
        List<ProductResponseDTO> response = productService.getProductsByStatus(status);
        return ResponseEntity.ok(response);
    }

    // ✅ Get Products By SubCategory - HTTP 200
    @GetMapping("/sub-category/{subCategoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsBySubCategory(@PathVariable Long subCategoryId) {
        List<ProductResponseDTO> response = productService.getProductsBySubCategory(subCategoryId);
        return ResponseEntity.ok(response);
    }

    // ✅ Update Product - HTTP 200
    @PutMapping("/{id}/vendor/{vendorId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @PathVariable Long vendorId,
            @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto, vendorId);
        return ResponseEntity.ok(response);
    }

    // ✅ Delete Product - HTTP 204
    @DeleteMapping("/{id}/vendor/{vendorId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @PathVariable Long vendorId) {
        productService.deleteProduct(id, vendorId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Change Product Status (Admin) - HTTP 200
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponseDTO> changeProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        ProductResponseDTO response = productService.changeProductStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // ✅ Search Products - HTTP 200 (Basic Implementation)
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam String keyword) {
        // Simple search implementation - filter existing products
        List<ProductResponseDTO> allProducts = productService.getAllProducts();
        List<ProductResponseDTO> filteredProducts = allProducts.stream()
                .filter(product ->
                        product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                (product.getDescription() != null &&
                                        product.getDescription().toLowerCase().contains(keyword.toLowerCase())) ||
                                (product.getBrand() != null &&
                                        product.getBrand().toLowerCase().contains(keyword.toLowerCase()))
                )
                .toList();
        return ResponseEntity.ok(filteredProducts);
    }

    // ✅ Filter Products - HTTP 200 (Basic Implementation)
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponseDTO>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String brand) {

        List<ProductResponseDTO> allProducts = productService.getAllProducts();

        List<ProductResponseDTO> filteredProducts = allProducts.stream()
                .filter(product ->
                        (categoryId == null ||
                                (product.getCategoryName() != null && productService.getProductsByCategory(categoryId).contains(product))) &&
                                (subCategoryId == null ||
                                        (product.getSubCategoryName() != null && productService.getProductsBySubCategory(subCategoryId).contains(product))) &&
                                (minPrice == null || product.getPrice().doubleValue() >= minPrice) &&
                                (maxPrice == null || product.getPrice().doubleValue() <= maxPrice) &&
                                (brand == null || brand.isEmpty() ||
                                        (product.getBrand() != null && product.getBrand().equalsIgnoreCase(brand)))
                )
                .toList();

        return ResponseEntity.ok(filteredProducts);
    }
}
