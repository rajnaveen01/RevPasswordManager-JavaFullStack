package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.dto.VaultEntryDTO;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.entity.VaultEntry;
import com.revpasswordmanager.repository.CategoryRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.repository.VaultEntryRepository;
import com.revpasswordmanager.service.VaultService;
import com.revpasswordmanager.util.EncryptionUtil;
import com.revpasswordmanager.util.PasswordStrengthUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VaultServiceImpl — Manages all CRUD operations on the password vault.
 *
 * Logging strategy:
 *   INFO  — successful vault operations (add, update, delete, favorite)
 *   DEBUG — internal state details (entry IDs, user lookups)
 *   WARN  — rejected operations (validation failures, duplicates)
 *   ERROR — unexpected failures
 */
@Service
public class VaultServiceImpl implements VaultService {

    private static final Logger logger = LogManager.getLogger(VaultServiceImpl.class);

    private final VaultEntryRepository vaultEntryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public VaultServiceImpl(VaultEntryRepository vaultEntryRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            PasswordEncoder passwordEncoder) {
        this.vaultEntryRepository = vaultEntryRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── VALIDATION HELPERS ───────────────────────────────────────────────────

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            logger.warn("Vault entry rejected — password is empty");
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (password.length() < 6) {
            logger.warn("Vault entry rejected — password too short (length: {})", password.length());
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            logger.warn("Vault entry rejected — password contains no letters");
            throw new IllegalArgumentException("Password must contain at least one letter.");
        }
    }

    private void validateNotMasterPassword(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPasswordHash() != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Vault entry rejected — password matches master password for userId: [{}]", userId);
            throw new IllegalArgumentException(
                    "Vault password cannot be the same as your master password.");
        }
    }

    // ── ADD VAULT ENTRY ──────────────────────────────────────────────────────

    @Override
    public void addVaultEntry(VaultEntryDTO dto) {
        logger.info("Adding vault entry — userId: [{}], accountName: [{}]",
                dto.getUserId(), dto.getAccountName());

        validatePassword(dto.getEncryptedPassword());
        validateNotMasterPassword(dto.getUserId(), dto.getEncryptedPassword());

        vaultEntryRepository
                .findByUser_IdAndAccountNameIgnoreCaseAndUsernameIgnoreCase(
                        dto.getUserId(), dto.getAccountName(), dto.getUsername())
                .ifPresent(existing -> {
                    logger.warn("Duplicate vault entry blocked — userId: [{}], account: [{}], username: [{}]",
                            dto.getUserId(), dto.getAccountName(), dto.getUsername());
                    throw new IllegalArgumentException(
                            "An entry for \"" + existing.getAccountName() +
                            "\" with username \"" + existing.getUsername() + "\" already exists.");
                });

        VaultEntry entry = new VaultEntry();
        entry.setAccountName(dto.getAccountName());
        entry.setWebsiteUrl(dto.getWebsiteUrl());
        entry.setUsername(dto.getUsername());
        entry.setNotes(dto.getNotes());

        String password = dto.getEncryptedPassword();
        entry.setEncryptedPassword(EncryptionUtil.encrypt(password));
        String strength = PasswordStrengthUtil.checkStrength(password);
        entry.setPasswordStrength(strength);
        logger.debug("Password strength evaluated as [{}] for accountName: [{}]",
                strength, dto.getAccountName());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        entry.setUser(user);

        resolveAndSetCategory(entry, dto.getCategoryName());

        entry.setUpdatedAt(LocalDateTime.now());
        vaultEntryRepository.save(entry);

        logger.info("Vault entry added successfully — userId: [{}], account: [{}]",
                dto.getUserId(), dto.getAccountName());
    }

    // ── GET ALL ENTRIES ──────────────────────────────────────────────────────

    @Override
    public List<VaultEntryDTO> getAllEntries(Long userId) {
        logger.debug("Fetching all vault entries for userId: [{}]", userId);
        List<VaultEntryDTO> entries = vaultEntryRepository.findByUser_Id(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Returned [{}] vault entries for userId: [{}]", entries.size(), userId);
        return entries;
    }

    // ── GET FAVORITES ────────────────────────────────────────────────────────

    @Override
    public List<VaultEntryDTO> getFavoriteEntries(Long userId) {
        logger.debug("Fetching favorite vault entries for userId: [{}]", userId);
        List<VaultEntryDTO> favorites = vaultEntryRepository.findByUser_IdAndIsFavoriteTrue(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Returned [{}] favorite entries for userId: [{}]", favorites.size(), userId);
        return favorites;
    }

    // ── UPDATE VAULT ENTRY ───────────────────────────────────────────────────

    @Override
    public void updateVaultEntry(Long entryId, VaultEntryDTO dto) {
        logger.info("Updating vault entry — entryId: [{}]", entryId);

        VaultEntry entry = vaultEntryRepository.findById(entryId)
                .orElseThrow(() -> {
                    logger.error("Update failed — entry not found: [{}]", entryId);
                    return new RuntimeException("Entry not found");
                });

        if (dto.getEncryptedPassword() != null && !dto.getEncryptedPassword().trim().isEmpty()) {
            validatePassword(dto.getEncryptedPassword());
            validateNotMasterPassword(entry.getUser().getId(), dto.getEncryptedPassword());
        }

        if (dto.getAccountName() != null && dto.getUsername() != null) {
            List<VaultEntry> duplicates = vaultEntryRepository
                    .findByUser_IdAndAccountNameIgnoreCaseAndUsernameIgnoreCaseAndIdNot(
                            entry.getUser().getId(), dto.getAccountName(), dto.getUsername(), entryId);
            if (!duplicates.isEmpty()) {
                logger.warn("Update blocked — duplicate entry for account: [{}], username: [{}]",
                        dto.getAccountName(), dto.getUsername());
                throw new IllegalArgumentException(
                        "Another entry for \"" + dto.getAccountName() +
                        "\" with username \"" + dto.getUsername() + "\" already exists.");
            }
        }

        if (dto.getAccountName() != null)  entry.setAccountName(dto.getAccountName());
        if (dto.getWebsiteUrl() != null)   entry.setWebsiteUrl(dto.getWebsiteUrl());
        if (dto.getUsername() != null)     entry.setUsername(dto.getUsername());
        if (dto.getNotes() != null)        entry.setNotes(dto.getNotes());

        if (dto.getEncryptedPassword() != null && !dto.getEncryptedPassword().trim().isEmpty()) {
            String password = dto.getEncryptedPassword();
            entry.setEncryptedPassword(EncryptionUtil.encrypt(password));
            entry.setPasswordStrength(PasswordStrengthUtil.checkStrength(password));
        }

        resolveAndSetCategory(entry, dto.getCategoryName());
        entry.setUpdatedAt(LocalDateTime.now());
        vaultEntryRepository.save(entry);

        logger.info("Vault entry updated successfully — entryId: [{}]", entryId);
    }

    // ── DELETE VAULT ENTRY ───────────────────────────────────────────────────

    @Override
    public void deleteVaultEntry(Long entryId) {
        logger.info("Deleting vault entry — entryId: [{}]", entryId);
        vaultEntryRepository.deleteById(entryId);
        logger.info("Vault entry deleted — entryId: [{}]", entryId);
    }

    // ── MARK AS FAVORITE ─────────────────────────────────────────────────────

    @Override
    public void markAsFavorite(Long entryId, Boolean favorite) {
        logger.info("Updating favorite status — entryId: [{}], favorite: [{}]", entryId, favorite);

        VaultEntry entry = vaultEntryRepository.findById(entryId)
                .orElseThrow(() -> {
                    logger.error("Favorite update failed — entry not found: [{}]", entryId);
                    return new RuntimeException("Entry not found");
                });

        entry.setIsFavorite(favorite);
        vaultEntryRepository.save(entry);

        logger.info("Favorite status updated — entryId: [{}], isFavorite: [{}]", entryId, favorite);
    }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────

    private void resolveAndSetCategory(VaultEntry entry, String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            com.revpasswordmanager.entity.Category category =
                categoryRepository.findByNameIgnoreCase(categoryName)
                    .orElseGet(() -> {
                        com.revpasswordmanager.entity.Category newCat =
                                new com.revpasswordmanager.entity.Category();
                        newCat.setName(categoryName.toUpperCase());
                        logger.debug("Creating new category: [{}]", categoryName.toUpperCase());
                        return categoryRepository.save(newCat);
                    });
            entry.setCategory(category);
        }
    }

    private VaultEntryDTO convertToDTO(VaultEntry entry) {
        VaultEntryDTO dto = new VaultEntryDTO();
        dto.setId(entry.getId());
        dto.setUserId(entry.getUser().getId());
        dto.setAccountName(entry.getAccountName());
        dto.setWebsiteUrl(entry.getWebsiteUrl());
        dto.setUsername(entry.getUsername());

        String password;
        try {
            password = EncryptionUtil.decrypt(entry.getEncryptedPassword());
        } catch (Exception e) {
            logger.error("Decryption failed for entryId: [{}] — returning raw value", entry.getId());
            password = entry.getEncryptedPassword();
        }

        dto.setEncryptedPassword(password);
        dto.setNotes(entry.getNotes());
        dto.setIsFavorite(entry.getIsFavorite());
        dto.setPasswordStrength(entry.getPasswordStrength());

        if (entry.getCategory() != null) {
            dto.setCategoryId(entry.getCategory().getId());
            dto.setCategoryName(entry.getCategory().getName());
        }

        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());

        return dto;
    }
}