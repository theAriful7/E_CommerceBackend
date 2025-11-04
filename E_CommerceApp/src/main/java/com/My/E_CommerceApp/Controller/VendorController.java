package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.VendorCreateRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.VendorRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.VendorUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.VendorResponseDTO;
import com.My.E_CommerceApp.Enum.VendorStatus;
import com.My.E_CommerceApp.Service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VendorController {

    private final VendorService vendorService;

    // 🔹 VENDOR REGISTRATION ENDPOINTS
    @PostMapping("/register")
    public ResponseEntity<VendorResponseDTO> createVendorWithNewUser(
            @Valid @RequestBody VendorCreateRequestDTO vendorCreateRequestDTO) {
        VendorResponseDTO vendor = vendorService.createVendorWithNewUser(vendorCreateRequestDTO);
        return new ResponseEntity<>(vendor, HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/register")
    public ResponseEntity<VendorResponseDTO> createVendorForExistingUser(
            @PathVariable Long userId,
            @Valid @RequestBody VendorRequestDTO vendorRequestDTO) {
        VendorResponseDTO vendor = vendorService.createVendorForExistingUser(userId, vendorRequestDTO);
        return new ResponseEntity<>(vendor, HttpStatus.CREATED);
    }

    // 🔹 VENDOR PROFILE ENDPOINTS
    @GetMapping("/{vendorId}")
    public ResponseEntity<VendorResponseDTO> getVendorById(@PathVariable Long vendorId) {
        VendorResponseDTO vendor = vendorService.getVendorById(vendorId);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<VendorResponseDTO> getVendorByUserId(@PathVariable Long userId) {
        VendorResponseDTO vendor = vendorService.getVendorByUserId(userId);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/shop/{shopName}")
    public ResponseEntity<VendorResponseDTO> getVendorByShopName(@PathVariable String shopName) {
        VendorResponseDTO vendor = vendorService.getVendorByShopName(shopName);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping
    public ResponseEntity<List<VendorResponseDTO>> getAllVendors() {
        List<VendorResponseDTO> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<VendorResponseDTO>> getAllVendorsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VendorResponseDTO> vendors = vendorService.getAllVendors(pageable);
        return ResponseEntity.ok(vendors);
    }

    // 🔹 VENDOR STATUS MANAGEMENT ENDPOINTS
    @PatchMapping("/{vendorId}/status")
    public ResponseEntity<VendorResponseDTO> updateVendorStatus(
            @PathVariable Long vendorId,
            @RequestParam VendorStatus newStatus) {
        VendorResponseDTO vendor = vendorService.updateVendorStatus(vendorId, newStatus);
        return ResponseEntity.ok(vendor);
    }

    @PatchMapping("/{vendorId}/approve")
    public ResponseEntity<VendorResponseDTO> approveVendor(@PathVariable Long vendorId) {
        VendorResponseDTO vendor = vendorService.approveVendor(vendorId);
        return ResponseEntity.ok(vendor);
    }

    @PatchMapping("/{vendorId}/suspend")
    public ResponseEntity<VendorResponseDTO> suspendVendor(@PathVariable Long vendorId) {
        VendorResponseDTO vendor = vendorService.suspendVendor(vendorId);
        return ResponseEntity.ok(vendor);
    }

    @PatchMapping("/{vendorId}/reject")
    public ResponseEntity<VendorResponseDTO> rejectVendor(@PathVariable Long vendorId) {
        VendorResponseDTO vendor = vendorService.rejectVendor(vendorId);
        return ResponseEntity.ok(vendor);
    }

    // 🔹 VENDOR PROFILE UPDATE ENDPOINTS
    @PutMapping("/{vendorId}")
    public ResponseEntity<VendorResponseDTO> updateVendorProfile(
            @PathVariable Long vendorId,
            @Valid @RequestBody VendorUpdateRequestDTO updateRequestDTO) {
        VendorResponseDTO vendor = vendorService.updateVendorProfile(vendorId, updateRequestDTO);
        return ResponseEntity.ok(vendor);
    }

    // 🔹 SEARCH & FILTER ENDPOINTS
    @GetMapping("/search")
    public ResponseEntity<List<VendorResponseDTO>> searchVendors(@RequestParam String keyword) {
        List<VendorResponseDTO> vendors = vendorService.searchVendors(keyword);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<Page<VendorResponseDTO>> searchVendorsPaginated(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorResponseDTO> vendors = vendorService.searchVendors(keyword, pageable);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VendorResponseDTO>> getVendorsByStatus(@PathVariable VendorStatus status) {
        List<VendorResponseDTO> vendors = vendorService.getVendorsByStatus(status);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<Page<VendorResponseDTO>> getVendorsByStatusPaginated(
            @PathVariable VendorStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorResponseDTO> vendors = vendorService.getVendorsByStatus(status, pageable);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/active")
    public ResponseEntity<List<VendorResponseDTO>> getActiveVendors() {
        List<VendorResponseDTO> vendors = vendorService.getActiveVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<List<VendorResponseDTO>> getPendingApprovalVendors() {
        List<VendorResponseDTO> vendors = vendorService.getPendingApprovalVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<VendorResponseDTO>> getTopRatedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<VendorResponseDTO> vendors = vendorService.getTopRatedVendors(pageable);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/rating/min/{minRating}")
    public ResponseEntity<List<VendorResponseDTO>> getVendorsWithMinRating(@PathVariable Double minRating) {
        List<VendorResponseDTO> vendors = vendorService.getVendorsWithMinRating(minRating);
        return ResponseEntity.ok(vendors);
    }

    // 🔹 UTILITY & ANALYTICS ENDPOINTS
    @GetMapping("/{vendorId}/active")
    public ResponseEntity<Boolean> isVendorActive(@PathVariable Long vendorId) {
        boolean isActive = vendorService.isVendorActive(vendorId);
        return ResponseEntity.ok(isActive);
    }

    @GetMapping("/shop/{shopName}/exists")
    public ResponseEntity<Boolean> vendorExistsByShopName(@PathVariable String shopName) {
        boolean exists = vendorService.vendorExistsByShopName(shopName);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> vendorExistsByUserId(@PathVariable Long userId) {
        boolean exists = vendorService.vendorExistsByUserId(userId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveVendorsCount() {
        Long count = vendorService.getActiveVendorsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getVendorsCountByStatus(@PathVariable VendorStatus status) {
        Long count = vendorService.getVendorsCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/pending-approval")
    public ResponseEntity<Long> getPendingApprovalCount() {
        Long count = vendorService.getPendingApprovalCount();
        return ResponseEntity.ok(count);
    }

    // 🔹 VENDOR RATING ENDPOINTS
    @PatchMapping("/{vendorId}/rating")
    public ResponseEntity<Void> updateVendorRating(
            @PathVariable Long vendorId,
            @RequestParam Double rating) {
        vendorService.updateVendorRating(vendorId, rating);
        return ResponseEntity.ok().build();
    }
}
