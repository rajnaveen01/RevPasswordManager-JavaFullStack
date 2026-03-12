package com.revpasswordmanager.util;

public class PasswordStrengthUtil {

    public static String checkStrength(String password) {
        if (password == null || password.isEmpty()) return "Weak";

        int score = 0;
        
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++; // Bonus for longer passwords
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+].*")) score++;

        if (score <= 2) return "Weak";
        if (score == 3 || score == 4) return "Medium";
        if (score == 5) return "Strong";
        
        return "Very Strong";
    }
}