package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.LoginRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.RegisterRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.UserResponseDTO;
import com.My.E_CommerceApp.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // ---------------- LOGIN ---------------- //
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        return userService.login(dto)
                .<ResponseEntity<?>>map(user -> {
                    // Role info আছে, frontend decide করবে redirect
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.status(401).body("Invalid email or password"));
    }

    // ---------------- REGISTER ---------------- //
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
        // সব নতুন user হবেন CUSTOMER role
        return ResponseEntity.ok(userService.register(dto));
    }
}
