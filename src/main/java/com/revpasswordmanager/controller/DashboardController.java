package com.revpasswordmanager.controller;

import com.revpasswordmanager.entity.VaultEntry;
import com.revpasswordmanager.repository.VaultEntryRepository;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final VaultEntryRepository vaultEntryRepository;

    public DashboardController(VaultEntryRepository vaultEntryRepository) {
        this.vaultEntryRepository = vaultEntryRepository;
    }

    @GetMapping("/summary/{userId}")
    public Map<String,Object> getDashboardSummary(@PathVariable Long userId){

        List<VaultEntry> entries = vaultEntryRepository.findByUser_Id(userId);

        // Calculate Counts
        long totalPasswords = entries.size();
        long weakPasswords = entries.stream().filter(e -> "Weak".equalsIgnoreCase(e.getPasswordStrength())).count();
        long mediumPasswords = entries.stream().filter(e -> "Medium".equalsIgnoreCase(e.getPasswordStrength())).count();
        long strongPasswords = entries.stream().filter(e -> "Strong".equalsIgnoreCase(e.getPasswordStrength()) || "Very Strong".equalsIgnoreCase(e.getPasswordStrength())).count();
        long favoritePasswords = entries.stream().filter(e -> Boolean.TRUE.equals(e.getIsFavorite())).count();

        // Extract Account Names as Lists
        List<String> totalAccounts = entries.stream().map(VaultEntry::getAccountName).toList();
        List<String> weakAccounts = entries.stream().filter(e -> "Weak".equalsIgnoreCase(e.getPasswordStrength())).map(VaultEntry::getAccountName).toList();
        List<String> mediumAccounts = entries.stream().filter(e -> "Medium".equalsIgnoreCase(e.getPasswordStrength())).map(VaultEntry::getAccountName).toList();
        List<String> strongAccounts = entries.stream().filter(e -> "Strong".equalsIgnoreCase(e.getPasswordStrength()) || "Very Strong".equalsIgnoreCase(e.getPasswordStrength())).map(VaultEntry::getAccountName).toList();
        List<String> favoriteAccounts = entries.stream().filter(e -> Boolean.TRUE.equals(e.getIsFavorite())).map(VaultEntry::getAccountName).toList();

        Map<String,Object> summary = new HashMap<>();
        summary.put("totalPasswords", totalPasswords);
        summary.put("weakPasswords", weakPasswords);
        summary.put("mediumPasswords", mediumPasswords);
        summary.put("strongPasswords", strongPasswords);
        summary.put("favoritePasswords", favoritePasswords);
        
        // Add the account lists to the JSON payload
        summary.put("totalAccounts", totalAccounts);
        summary.put("weakAccounts", weakAccounts);
        summary.put("mediumAccounts", mediumAccounts);
        summary.put("strongAccounts", strongAccounts);
        summary.put("favoriteAccounts", favoriteAccounts);

        return summary;
    }
}