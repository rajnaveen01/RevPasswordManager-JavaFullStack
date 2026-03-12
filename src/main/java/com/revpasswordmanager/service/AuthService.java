package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.LoginRequestDTO;
import com.revpasswordmanager.dto.RegisterRequestDTO;

public interface AuthService {

    // Register new user
    void registerUser(RegisterRequestDTO dto);

    // Login user and return userId
    Long login(LoginRequestDTO dto);

    // Change master password
    void changeMasterPassword(String username, String currentPassword, String newPassword);

    // Enable / Disable Two Factor Authentication
    void toggleTwoFactor(String username, Boolean enabled);

    // Recover forgotten password
    void recoverPassword(String username);

    // Logout user
    void logout();
}