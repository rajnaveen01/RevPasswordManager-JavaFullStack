package com.revpasswordmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vault_entry")
public class VaultEntry {
	

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="account_name")
    private String accountName;

    @Column(name="website_url")
    private String websiteUrl;

    private String username;

    @Column(name="encrypted_password", nullable = false)
    private String encryptedPassword;

    @Column(length = 1000)
    private String notes;

    @Column(name="is_favorite")
    private Boolean isFavorite = false;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name="password_strength")
    private String passwordStrength;

    public String getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(String passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public VaultEntry() {}

    public Long getId() { return id; }

    public String getAccountName() { return accountName; }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getWebsiteUrl() { return websiteUrl; }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEncryptedPassword() { return encryptedPassword; }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsFavorite() { return isFavorite; }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) {
        this.category = category;
    }
    

}