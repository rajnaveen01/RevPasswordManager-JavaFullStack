package com.revpasswordmanager.serviceimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Handles LocalDateTime
import com.revpasswordmanager.dto.VaultEntryDTO;
import com.revpasswordmanager.entity.BackupFile;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.repository.BackupFileRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.service.BackupService;
import com.revpasswordmanager.service.VaultService;
import com.revpasswordmanager.util.EncryptionUtil;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackupServiceImpl implements BackupService {

    private final BackupFileRepository backupRepository;
    private final UserRepository userRepository;
    private final VaultService vaultService;
    private final ObjectMapper objectMapper;

    public BackupServiceImpl(BackupFileRepository backupRepository,
                             UserRepository userRepository,
                             VaultService vaultService) {
        this.backupRepository = backupRepository;
        this.userRepository = userRepository;
        this.vaultService = vaultService;
        
        // FIX: Configure ObjectMapper to perfectly handle LocalDateTime and Categories
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String exportVault(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            List<VaultEntryDTO> entries = vaultService.getAllEntries(userId);

            // Convert to JSON String (now safely handles Dates)
            String jsonPayload = objectMapper.writeValueAsString(entries);

            // Encrypt the JSON payload
            String encryptedData = EncryptionUtil.encrypt(jsonPayload);

            // Save backup record
            BackupFile backup = new BackupFile();
            backup.setFileName("backup_" + userId + "_" + System.currentTimeMillis() + ".enc");
            backup.setEncryptedData(encryptedData);
            backup.setUser(user);
            backupRepository.save(backup);

            return encryptedData;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export vault: " + e.getMessage());
        }
    }

    @Override
    public void importVault(Long userId, String encryptedData) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // 1. Decrypt Data
            String decryptedJson = EncryptionUtil.decrypt(encryptedData);

            // 2. Read back into DTO List
            List<VaultEntryDTO> entries = objectMapper.readValue(
                    decryptedJson, 
                    new TypeReference<List<VaultEntryDTO>>() {}
            );

            // 3. Save to database safely
            for (VaultEntryDTO entry : entries) {
                entry.setUserId(userId);
                entry.setId(null); // FIX: Reset ID to null so Hibernate creates a new row instead of overwriting!
                
                vaultService.addVaultEntry(entry);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to import vault. Invalid encryption key or corrupted file.");
        }
    }
}