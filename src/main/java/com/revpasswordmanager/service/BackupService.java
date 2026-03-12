package com.revpasswordmanager.service;

public interface BackupService {

    String exportVault(Long userId);

    void importVault(Long userId, String encryptedData);

}