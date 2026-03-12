package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.VaultEntryDTO;
import java.util.List;

public interface VaultService {

    void addVaultEntry(VaultEntryDTO dto);

    List<VaultEntryDTO> getAllEntries(Long userId);

    List<VaultEntryDTO> getFavoriteEntries(Long userId);

    void updateVaultEntry(Long entryId, VaultEntryDTO dto);

    void deleteVaultEntry(Long entryId);

    void markAsFavorite(Long entryId, Boolean favorite);
}