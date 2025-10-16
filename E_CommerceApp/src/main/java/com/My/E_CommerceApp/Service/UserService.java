package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.LoginRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.RegisterRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.UserRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.UserResponseDTO;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Enum.Role;
import com.My.E_CommerceApp.Repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepo userRepo;

    // 🧱 Static Users (Demo Mode)
    private final List<UserResponseDTO> staticUsers = new ArrayList<>();

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;

        // 🟢 Predefined demo users (password: 1234)
        UserResponseDTO admin = new UserResponseDTO();
        admin.setId(1L);
        admin.setFullName("Admin User");
        admin.setEmail("admin@gmail.com");
        admin.setRole(Role.ADMIN);

        UserResponseDTO vendor = new UserResponseDTO();
        vendor.setId(2L);
        vendor.setFullName("Vendor User");
        vendor.setEmail("vendor@gmail.com");
        vendor.setRole(Role.VENDOR);

        UserResponseDTO customer = new UserResponseDTO();
        customer.setId(3L);
        customer.setFullName("Customer User");
        customer.setEmail("customer@gmail.com");
        customer.setRole(Role.CUSTOMER);

        staticUsers.add(admin);
        staticUsers.add(vendor);
        staticUsers.add(customer);
    }

    // ------------------ EXISTING CRUD ------------------ //

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
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

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User saved = userRepo.save(toEntity(dto));
        return toDto(saved);
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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

    public String deleteUser(Long id) {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setIsActive(false);
        userRepo.save(existing);
        return "User deactivated successfully";
    }

    // ------------------ NEW LOGIN & REGISTER ------------------ //

    public Optional<UserResponseDTO> login(LoginRequestDTO dto) {
        // ✅ 1️⃣ Check Static Demo Users
        Optional<UserResponseDTO> staticMatch = staticUsers.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail())
                        && dto.getPassword().equals("1234"))
                .findFirst();

        if (staticMatch.isPresent()) return staticMatch;

        // ✅ 2️⃣ Check Database Users
        return userRepo.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail())
                        && dto.getPassword().equals(u.getPasswordHash()))
                .map(this::toDto)
                .findFirst();
    }

    public UserResponseDTO register(RegisterRequestDTO dto) {
        // 🟢 All new users = CUSTOMER
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(dto.getPassword());
        user.setRole(Role.CUSTOMER);

        User saved = userRepo.save(user);
        return toDto(saved);
    }

}
