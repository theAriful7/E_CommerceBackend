package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.ProductRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ProductResponseDTO;
import com.My.E_CommerceApp.Entity.FileData;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Enum.ProductStatus;
import com.My.E_CommerceApp.Service.FileDataService;
import com.My.E_CommerceApp.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
//@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileDataService fileDataService;

    @PostMapping("/vendor/{vendorId}")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @PathVariable Long vendorId,
            @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto, vendorId);
        return ResponseEntity.ok(response); // ✅ Fix: HTTP 200
    }

    // ✅ UPDATED: Upload Product Images
//    @PostMapping("/{productId}/images")
//    public ResponseEntity<?> uploadProductImages(
//            @PathVariable Long productId,
//            @RequestParam("files") List<MultipartFile> files,
//            @RequestParam(value = "altTexts", required = false) List<String> altTexts,
//            @RequestParam(value = "sortOrders", required = false) List<Integer> sortOrders,
//            @RequestParam(value = "isPrimary", required = false) List<Boolean> isPrimary) {
//
//        try {
//            List<FileData> uploadedImages = fileDataService.uploadProductImages(
//                    productId, files, altTexts, sortOrders, isPrimary
//            );
//            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error uploading images: " + e.getMessage());
//        }
//    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<List<FileData>> uploadProductImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "altTexts", required = false) List<String> altTexts,
            @RequestParam(value = "sortOrders", required = false) List<Integer> sortOrders,
            @RequestParam(value = "isPrimary", required = false) List<Boolean> isPrimary) throws IOException {

        List<FileData> uploadedImages = fileDataService.uploadProductImages(
                productId, files, altTexts, sortOrders, isPrimary
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);
    }

    // ✅ UPDATED: Get Product Images
    @GetMapping("/{productId}/images")
    public ResponseEntity<?> getProductImages(@PathVariable Long productId) {
        try {
            List<FileData> images = fileDataService.getProductImages(productId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving images: " + e.getMessage());
        }
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

    // ✅ Search Products
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDTO> response = productService.searchProducts(keyword);
        return ResponseEntity.ok(response);
    }

    // ✅ Filter Products
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponseDTO>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String brand) {

        List<ProductResponseDTO> response = productService.filterProducts(
                categoryId, subCategoryId, minPrice, maxPrice, brand
        );
        return ResponseEntity.ok(response);
    }

    // ✅ UPDATED: Delete Product Image
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<?> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            fileDataService.removeImageFromProduct(productId, imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting image: " + e.getMessage());
        }
    }

    // ✅ UPDATED: Set Primary Image
    @PatchMapping("/{productId}/images/{imageId}/primary")
    public ResponseEntity<?> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            FileData result = fileDataService.setPrimaryImage(productId, imageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error setting primary image: " + e.getMessage());
        }
    }

    @GetMapping("/home/trending")
    public ResponseEntity<List<ProductResponseDTO>> getTrendingProducts(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<Product> products = productService.getTrendingProducts(limit);
            List<ProductResponseDTO> response = products.stream()
                    .map(productService::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/home/bestsellers")
    public ResponseEntity<List<ProductResponseDTO>> getBestSellers(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<Product> products = productService.getBestSellers(limit);
            List<ProductResponseDTO> response = products.stream()
                    .map(productService::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/home/featured")
    public ResponseEntity<List<ProductResponseDTO>> getFeaturedProducts(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<Product> products = productService.getFeaturedProducts(limit);
            List<ProductResponseDTO> response = products.stream()
                    .map(productService::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/home/all")
    public ResponseEntity<Map<String, List<ProductResponseDTO>>> getHomePageProducts(
            @RequestParam(defaultValue = "8") int trendingLimit,
            @RequestParam(defaultValue = "8") int bestsellersLimit,
            @RequestParam(defaultValue = "8") int featuredLimit) {
        try {
            Map<String, List<ProductResponseDTO>> result = new HashMap<>();

            result.put("trending", productService.getTrendingProducts(trendingLimit).stream()
                    .map(productService::toDto)
                    .collect(Collectors.toList()));

            result.put("bestsellers", productService.getBestSellers(bestsellersLimit).stream()
                    .map(productService::toDto)
                    .collect(Collectors.toList()));

            result.put("featured", productService.getFeaturedProducts(featuredLimit).stream()
                    .map(productService::toDto)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
