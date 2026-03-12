package com.revpasswordmanager.config;

import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class TwoFactorConfig {

    private static final SecureRandom random = new SecureRandom();

    public String generateOTP() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}