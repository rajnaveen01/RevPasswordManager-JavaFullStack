package com.revpasswordmanager.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String code);
}