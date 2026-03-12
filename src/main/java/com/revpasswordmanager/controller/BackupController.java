package com.revpasswordmanager.controller;

import com.revpasswordmanager.service.BackupService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/export/{userId}")
    public String exportVault(@PathVariable Long userId) {

        return backupService.exportVault(userId);
    }

    @PostMapping("/import/{userId}")
    public String importVault(@PathVariable Long userId,
                              @RequestBody String encryptedData) {

        backupService.importVault(userId, encryptedData);

        return "Vault imported successfully";
    }

}