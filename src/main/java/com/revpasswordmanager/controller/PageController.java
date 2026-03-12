package com.revpasswordmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Home Page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Register Page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Dashboard Page
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    // Password Vault Page
    @GetMapping("/vault")
    public String vaultPage() {
        return "vault";
    }

    // Add Password Page
    @GetMapping("/vault/add")
    public String addPasswordPage() {
        return "add-password";
    }

    // Password Generator Page
    @GetMapping("/generator")
    public String generatorPage() {
        return "generator";
    }
    @GetMapping("/backup")
    public String backupPage() {
        return "backup";
    }

    // Security Audit Page
    @GetMapping("/security-report")
    public String securityReportPage() {
        return "security-report";
    }
    
    // Forgot Password Page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }
     
    //profile usage
    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
    
    //verify-code 
    @GetMapping("/verify-code")
    public String verifyCodePage() {
        return "verify-code";
    }
}