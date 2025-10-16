package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.ProductRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ProductResponseDTO;
import com.My.E_CommerceApp.Enum.ProductStatus;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // -------------------- Create Product -------------------- //
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestBody ProductRequestDTO dto,
            @RequestParam Long vendorId) { // Vendor creating the product
        ProductResponseDTO response = productService.createProduct(dto, vendorId);
        return ResponseEntity.ok(response);
    }

    // -------------------- Get Product -------------------- //
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }

    // -------------------- Get Products By Vendor -------------------- //
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByVendor(@PathVariable Long vendorId) {
        List<ProductResponseDTO> response = productService.getProductsByVendor(vendorId);
        return ResponseEntity.ok(response);
    }

    // -------------------- Update Product -------------------- //
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestParam Long vendorId,
            @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto, vendorId);
        return ResponseEntity.ok(response);
    }

    // -------------------- Delete Product -------------------- //
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id,
            @RequestParam Long vendorId) {
        productService.deleteProduct(id, vendorId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // -------------------- Admin Only: Change Status -------------------- //
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponseDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        // Optionally, call a service method to change status only if admin
        ProductResponseDTO product = productService.getProductById(id);
        // You can implement admin check in service layer
        // product.setStatus(status);
        // return ResponseEntity.ok(productService.save(product));
        return ResponseEntity.ok(product);
    }
}
