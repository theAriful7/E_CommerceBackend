package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.AddressRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.AddressResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }


    // ➕ Create new address
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.createAddress(dto));
    }

    // 🔍 Get address by ID
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    // 📋 Get all addresses
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // ✏️ Update address
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long id, @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.updateAddress(id, dto));
    }

    // ❌ Delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.deleteAddress(id));
    }
}
