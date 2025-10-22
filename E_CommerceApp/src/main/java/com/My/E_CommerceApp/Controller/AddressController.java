package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.AddressRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.AddressResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // ➕ CREATE NEW ADDRESS
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.createAddress(dto));
    }

    // 🔍 GET ADDRESS BY ID
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    // 📋 GET ALL ADDRESSES
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // 👤 GET ADDRESSES BY USER ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserId(userId));
    }

    // ✏️ UPDATE ADDRESS
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.updateAddress(id, dto));
    }

    // ❌ DELETE ADDRESS (Simple - without user validation)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.deleteAddress(id));
    }

    // 🔒 DELETE ADDRESS WITH USER VALIDATION
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<String> deleteAddressWithValidation(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return ResponseEntity.ok(addressService.deleteAddress(id, userId));
    }

    // 🔢 GET ADDRESS COUNT BY USER
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> getAddressCount(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressCountByUser(userId));
    }

    // ✅ CHECK IF USER HAS ADDRESSES
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> userHasAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.userHasAddresses(userId));
    }

    // 🏠 GET DEFAULT ADDRESS
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getDefaultAddress(userId));
    }
}
