package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.AddressRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.AddressResponseDTO;
import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Repository.AddressRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepo addressRepo;

    public AddressService(AddressRepo addressRepo) {
        this.addressRepo = addressRepo;
    }

    private Address toEntity(AddressRequestDTO dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        return address;
    }

    private AddressResponseDTO toDto(Address entity) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(entity.getId());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setPostalCode(entity.getPostalCode());
        return dto;
    }


    // ➕ Create Address
    public AddressResponseDTO createAddress(AddressRequestDTO dto) {
        Address saved = addressRepo.save(toEntity(dto));
        return toDto(saved);
    }

    // 🔍 Get Address by ID
    public AddressResponseDTO getAddressById(Long id) {
        Optional<Address> address = addressRepo.findById(id);
        return address.map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));
    }

    // 📋 Get All Addresses
    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✏️ Update Address
    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO dto) {
        Address existing = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));

        existing.setStreet(dto.getStreet());
        existing.setCity(dto.getCity());
        existing.setCountry(dto.getCountry());
        existing.setPostalCode(dto.getPostalCode());

        Address updated = addressRepo.save(existing);
        return toDto(updated);
    }

    // ❌ Delete Address
    public String deleteAddress(Long id) {
        Address existing = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));
        addressRepo.delete(existing);
        return "Address deleted successfully.";
    }
}
