package com.revpasswordmanager.controller;

import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthVerifyController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthVerifyController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/verify-master")
    public Map<String, Boolean> verifyMasterPassword(@RequestParam Long userId,
                                                     @RequestParam String masterPassword){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean valid = encoder.matches(masterPassword, user.getPasswordHash());

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return response;
    }
}