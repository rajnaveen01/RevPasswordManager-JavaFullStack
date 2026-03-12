package com.revpasswordmanager.dto;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String accountStatus;
    private Boolean twoFactorEnabled;  // ✅ ADDED - was missing!

    public UserResponseDTO(Long id, String username, String email,
                           String phoneNumber, String accountStatus,
                           Boolean twoFactorEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAccountStatus() { return accountStatus; }
    public Boolean getTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(Boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
}