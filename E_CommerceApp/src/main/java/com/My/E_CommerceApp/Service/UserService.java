package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.UserRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.UserResponseDTO;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
//        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordHash(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }


    public UserResponseDTO toDto(User user) {
        UserResponseDTO res = new UserResponseDTO();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setRole(user.getRole());
        res.setIsActive(user.getIsActive());
        return res;
    }


    // ➕ Save user
    public UserResponseDTO createUser(UserRequestDTO dto) {
        User saved = userRepo.save(toEntity(dto));
        return toDto(saved);
    }

    // 🔍 Find by ID
    public UserResponseDTO getUserById(Long id) {
        Optional<User> user = userRepo.findById(id);
        return user.map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 📋 Get all users
    public List<UserResponseDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✏️ Update user
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setPasswordHash(dto.getPassword());
        existing.setRole(dto.getRole());
        User updated = userRepo.save(existing);
        return toDto(updated);
    }

    // ❌ Delete (soft delete)
    public String deleteUser(Long id) {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setIsActive(false);
        userRepo.save(existing);
        return "User deactivated successfully";
    }

}
