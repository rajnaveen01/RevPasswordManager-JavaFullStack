//package com.revpasswordmanager.controller;
//
//import com.revpasswordmanager.dto.LoginRequestDTO;
//import com.revpasswordmanager.dto.RegisterRequestDTO;
//import com.revpasswordmanager.service.AuthService;
//
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    // Register User
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
//        authService.registerUser(dto);
//        return ResponseEntity.ok("User registered successfully");
//    }
//
//    // Login
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
//        Long userId = authService.login(dto);
//        if (userId == -1) {
//            return ResponseEntity.ok("2FA_REQUIRED");
//        }
//        return ResponseEntity.ok(userId);
//    }
//
//    // Enable / Disable 2FA
//    @PutMapping("/2fa")
//    public ResponseEntity<String> toggle2FA(
//            @RequestParam String username,
//            @RequestParam Boolean enabled) {
//        authService.toggleTwoFactor(username, enabled);
//        return ResponseEntity.ok("2FA updated successfully");
//    }
//
//    // Change master password
//    @PutMapping("/change-password")
//    public ResponseEntity<String> changePassword(
//            @RequestParam String username,
//            @RequestParam String currentPassword,
//            @RequestParam String newPassword) {
//        authService.changeMasterPassword(username, currentPassword, newPassword);
//        return ResponseEntity.ok("Master password changed successfully");
//    }
//}



package com.revpasswordmanager.controller;

import com.revpasswordmanager.dto.LoginRequestDTO;
import com.revpasswordmanager.dto.RegisterRequestDTO;
import com.revpasswordmanager.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.registerUser(dto);
        return ResponseEntity.ok("User registered successfully");
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        Long userId = authService.login(dto);
        if (userId == -1) {
            return ResponseEntity.ok("2FA_REQUIRED");
        }
        return ResponseEntity.ok(userId);
    }

    // Enable / Disable 2FA
    @PutMapping("/2fa")
    public ResponseEntity<String> toggle2FA(
            @RequestParam String username,
            @RequestParam Boolean enabled) {
        authService.toggleTwoFactor(username, enabled);
        return ResponseEntity.ok("2FA updated successfully");
    }

    // Change master password
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String username,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        authService.changeMasterPassword(username, currentPassword, newPassword);
        return ResponseEntity.ok("Master password changed successfully");
    }
}