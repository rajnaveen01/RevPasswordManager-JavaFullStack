package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.VaultEntryDTO;
import com.revpasswordmanager.entity.Category;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.entity.VaultEntry;
import com.revpasswordmanager.repository.CategoryRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.repository.VaultEntryRepository;
import com.revpasswordmanager.serviceimpl.VaultServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VaultServiceImplTest {

    private static final Logger logger = LogManager.getLogger(VaultServiceImplTest.class);

    @Mock private VaultEntryRepository vaultEntryRepository;
    @Mock private UserRepository       userRepository;
    @Mock private CategoryRepository   categoryRepository;
    @Mock private PasswordEncoder      passwordEncoder;

    @InjectMocks
    private VaultServiceImpl vaultService;

    private User      testUser;
    private VaultEntry testEntry;

    @Before
    public void setUp() {
        logger.info("=== Setting up VaultServiceImplTest ===");

        testUser = new User();
        testUser.setUsername("john_doe");
        testUser.setPasswordHash("$2a$hashed_master");

        testEntry = new VaultEntry();
        testEntry.setAccountName("Gmail");
        testEntry.setWebsiteUrl("https://gmail.com");
        testEntry.setUsername("john@gmail.com");
        testEntry.setEncryptedPassword("ENCRYPTED_VAL");
        testEntry.setPasswordStrength("Strong");
        testEntry.setIsFavorite(false);
        testEntry.setCreatedAt(LocalDateTime.now());
        testEntry.setUpdatedAt(LocalDateTime.now());
        testEntry.setUser(testUser);
    }

    private VaultEntryDTO makeDTO() {
        VaultEntryDTO dto = new VaultEntryDTO();
        dto.setUserId(1L);
        dto.setAccountName("Gmail");
        dto.setWebsiteUrl("https://gmail.com");
        dto.setUsername("john@gmail.com");
        dto.setEncryptedPassword("SecurePass@123");
        dto.setCategoryName("EMAIL");
        dto.setNotes("Primary email");
        return dto;
    }

    // ── addVaultEntry ─────────────────────────────────────────────

    @Test
    public void addVaultEntry_validDTO_savesEntry() {
        logger.info("TEST: addVaultEntry_validDTO_savesEntry");

        VaultEntryDTO dto = makeDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(vaultEntryRepository
                .findByUser_IdAndAccountNameIgnoreCaseAndUsernameIgnoreCase(
                        anyLong(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCase("EMAIL"))
                .thenReturn(Optional.of(new Category()));

        // ✅ FIX: mock vaultEntryRepository.save() to return testEntry
        //    Mockito default = null. If service assigns: VaultEntry saved = repo.save(entry)
        //    and then calls saved.getId(), it NPEs. Mocking prevents this.
        when(vaultEntryRepository.save(any(VaultEntry.class))).thenReturn(testEntry);

        vaultService.addVaultEntry(dto);

        verify(vaultEntryRepository, times(1)).save(any(VaultEntry.class));
        logger.info("PASS");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVaultEntry_emptyPassword_throwsException() {
        logger.info("TEST: addVaultEntry_emptyPassword_throwsException");
        VaultEntryDTO dto = makeDTO();
        dto.setEncryptedPassword("");
        vaultService.addVaultEntry(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVaultEntry_tooShortPassword_throwsException() {
        logger.info("TEST: addVaultEntry_tooShortPassword_throwsException");
        VaultEntryDTO dto = makeDTO();
        dto.setEncryptedPassword("ab1");
        vaultService.addVaultEntry(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVaultEntry_matchesMasterPassword_throwsException() {
        logger.info("TEST: addVaultEntry_matchesMasterPassword_throwsException");
        VaultEntryDTO dto = makeDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(dto.getEncryptedPassword(), testUser.getPasswordHash()))
                .thenReturn(true);
        vaultService.addVaultEntry(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVaultEntry_duplicateEntry_throwsException() {
        logger.info("TEST: addVaultEntry_duplicateEntry_throwsException");
        VaultEntryDTO dto = makeDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(vaultEntryRepository
                .findByUser_IdAndAccountNameIgnoreCaseAndUsernameIgnoreCase(
                        anyLong(), anyString(), anyString()))
                .thenReturn(Optional.of(testEntry));
        vaultService.addVaultEntry(dto);
    }

    // ── getAllEntries ─────────────────────────────────────────────

    @Test
    public void getAllEntries_twoEntries_returnsTwo() {
        logger.info("TEST: getAllEntries_twoEntries_returnsTwo");

        VaultEntry e2 = new VaultEntry();
        e2.setAccountName("Netflix");
        e2.setEncryptedPassword("ENC2");
        e2.setUser(testUser);
        e2.setCreatedAt(LocalDateTime.now());
        e2.setUpdatedAt(LocalDateTime.now());

        when(vaultEntryRepository.findByUser_Id(1L))
                .thenReturn(Arrays.asList(testEntry, e2));

        List<VaultEntryDTO> result = vaultService.getAllEntries(1L);

        assertEquals(2, result.size());
        logger.info("PASS: {} entries returned", result.size());
    }

    @Test
    public void getAllEntries_noEntries_returnsEmptyList() {
        logger.info("TEST: getAllEntries_noEntries_returnsEmptyList");
        when(vaultEntryRepository.findByUser_Id(99L)).thenReturn(Collections.emptyList());
        List<VaultEntryDTO> result = vaultService.getAllEntries(99L);
        assertTrue(result.isEmpty());
        logger.info("PASS");
    }

    // ── getFavoriteEntries ────────────────────────────────────────

    @Test
    public void getFavoriteEntries_hasFavorites_returnsOne() {
        logger.info("TEST: getFavoriteEntries_hasFavorites_returnsOne");

        VaultEntry fav = new VaultEntry();
        fav.setAccountName("GitHub");
        fav.setIsFavorite(true);
        fav.setEncryptedPassword("ENC");
        fav.setUser(testUser);
        fav.setCreatedAt(LocalDateTime.now());
        fav.setUpdatedAt(LocalDateTime.now());

        when(vaultEntryRepository.findByUser_IdAndIsFavoriteTrue(1L))
                .thenReturn(Collections.singletonList(fav));

        List<VaultEntryDTO> result = vaultService.getFavoriteEntries(1L);

        assertEquals(1, result.size());
        logger.info("PASS");
    }

    // ── deleteVaultEntry ──────────────────────────────────────────

    @Test
    public void deleteVaultEntry_validId_callsRepository() {
        logger.info("TEST: deleteVaultEntry_validId_callsRepository");
        doNothing().when(vaultEntryRepository).deleteById(10L);
        vaultService.deleteVaultEntry(10L);
        verify(vaultEntryRepository, times(1)).deleteById(10L);
        logger.info("PASS");
    }

    // ── markAsFavorite ────────────────────────────────────────────

    @Test
    public void markAsFavorite_setsTrue() {
        logger.info("TEST: markAsFavorite_setsTrue");
        testEntry.setIsFavorite(false);
        when(vaultEntryRepository.findById(10L)).thenReturn(Optional.of(testEntry));
        vaultService.markAsFavorite(10L, true);
        assertTrue(testEntry.getIsFavorite());
        verify(vaultEntryRepository, times(1)).save(testEntry);
        logger.info("PASS");
    }

    @Test
    public void markAsFavorite_setsFalse() {
        logger.info("TEST: markAsFavorite_setsFalse");
        testEntry.setIsFavorite(true);
        when(vaultEntryRepository.findById(10L)).thenReturn(Optional.of(testEntry));
        vaultService.markAsFavorite(10L, false);
        assertFalse(testEntry.getIsFavorite());
        logger.info("PASS");
    }

    @Test(expected = RuntimeException.class)
    public void markAsFavorite_invalidId_throwsException() {
        logger.info("TEST: markAsFavorite_invalidId_throwsException");
        when(vaultEntryRepository.findById(999L)).thenReturn(Optional.empty());
        vaultService.markAsFavorite(999L, true);
    }
    
    
}