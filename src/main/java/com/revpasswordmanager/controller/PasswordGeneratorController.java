package com.revpasswordmanager.controller;

import com.revpasswordmanager.dto.PasswordGeneratorRequestDTO;
import com.revpasswordmanager.service.PasswordGeneratorService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/password-generator")
public class PasswordGeneratorController {

    private final PasswordGeneratorService generatorService;

    public PasswordGeneratorController(PasswordGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    // Generate single password
    @PostMapping("/generate")
    public String generatePassword(@RequestBody PasswordGeneratorRequestDTO dto) {

        return generatorService.generatePassword(dto);
    }

    // Generate multiple passwords
    @PostMapping("/generate-multiple")
    public List<String> generateMultiplePasswords(
            @RequestBody PasswordGeneratorRequestDTO dto,
            @RequestParam int count) {

        return generatorService.generateMultiplePasswords(dto, count);
    }

    // Check password strength
    @GetMapping("/strength")
    public String checkStrength(@RequestParam String password) {

        return generatorService.checkStrength(password);
    }
}