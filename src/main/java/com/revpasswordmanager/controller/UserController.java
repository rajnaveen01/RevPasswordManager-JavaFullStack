package com.revpasswordmanager.controller;

import com.revpasswordmanager.dto.UserResponseDTO;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Get user by username
    @GetMapping("/name/{username}")
    public ResponseEntity<UserResponseDTO> getUserByName(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByName(username));
    }

    // Create user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        UserResponseDTO createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        userService.updateUserById(id, updatedUser);
        return ResponseEntity.ok("User updated successfully");
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
    
    //2F verification
    @PutMapping("/toggle-2fa/{id}")
    public ResponseEntity<String> toggleTwoFactor(@PathVariable Long id) {

        userService.toggleTwoFactor(id);

        return ResponseEntity.ok("Two Factor Authentication updated");
    }
}