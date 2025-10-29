package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.LoginRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.UserRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.UserResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    // -------------------- 🔹 Register -------------------- //
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO user = userService.register(dto);
        return ResponseEntity.ok(user);
    }

    // -------------------- 🔹 Login -------------------- //
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        Optional<UserResponseDTO> userOpt = userService.login(dto);
        if (userOpt.isPresent()) return ResponseEntity.ok(userOpt.get());
        return ResponseEntity.badRequest().body("Invalid email or password");
    }

    // -------------------- 🔹 Get Users -------------------- //
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // -------------------- 🔹 Update Profile -------------------- //
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    // -------------------- 🔹 Deactivate User -------------------- //
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    // -------------------- 🔹 Promote User -------------------- //
    // Promote to Vendor
    @PatchMapping("/{id}/promote/vendor")
    public ResponseEntity<UserResponseDTO> promoteToVendor(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToVendor(id));
    }

    // Promote to Admin
    @PatchMapping("/{id}/promote/admin")
    public ResponseEntity<UserResponseDTO> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToAdmin(id));
    }
}
