package com.revpasswordmanager.controller;

import com.revpasswordmanager.dto.AuditReportDTO;
import com.revpasswordmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.revpasswordmanager.dto.SecurityAnswerDTO;
import com.revpasswordmanager.dto.SecurityQuestionDTO;
import com.revpasswordmanager.entity.SecurityAuditReport;
import com.revpasswordmanager.service.AuditService;
import com.revpasswordmanager.service.SecurityService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

	private final SecurityService securityService;
	private final AuditService auditService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SecurityController(SecurityService securityService, AuditService auditService,
	                           UserRepository userRepository, PasswordEncoder passwordEncoder) {
	    this.securityService = securityService;
	    this.auditService = auditService;
	    this.userRepository = userRepository;
	    this.passwordEncoder = passwordEncoder;
	}

    // Get security questions with IDs (for profile view/edit)
    @GetMapping("/questions-full/{userId}")
    public ResponseEntity<List<java.util.Map<String, Object>>> getSecurityQuestionsWithIds(@PathVariable Long userId) {
        return ResponseEntity.ok(securityService.getSecurityQuestionsWithIds(userId));
    }

    // Update a single security question answer
    @PutMapping("/question/{questionId}")
    public ResponseEntity<String> updateSecurityAnswer(
            @PathVariable Long questionId,
            @RequestBody SecurityQuestionDTO dto) {
        try {
            securityService.updateSecurityAnswer(questionId, dto.getAnswer());
            return ResponseEntity.ok("Security answer updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update answer");
        }
    }

    // Add security question
    @PostMapping("/question")
    public ResponseEntity<String> addSecurityQuestion(@RequestBody SecurityQuestionDTO dto) {
        securityService.addSecurityQuestion(dto);
        return ResponseEntity.ok("Security question added successfully");
    }

    // Get security questions
    @GetMapping("/questions/{userId}")
    public ResponseEntity<List<String>> getSecurityQuestions(@PathVariable Long userId) {
        return ResponseEntity.ok(securityService.getSecurityQuestions(userId));
    }

    // Generate verification code
    @PostMapping("/verification/{userId}")
    public ResponseEntity<String> generateVerificationCode(
            @PathVariable Long userId,
            @RequestParam String purpose) {
        String code = securityService.generateVerificationCode(userId, purpose);
        return ResponseEntity.ok("Verification Code: " + code);
    }

    // ✅ FIXED: Security audit report - now returns real JSON DTO
    @GetMapping("/audit/{userId}")
    public ResponseEntity<AuditReportDTO> getSecurityAudit(@PathVariable Long userId) {
        SecurityAuditReport report = auditService.generateAuditReport(userId);
        AuditReportDTO dto = new AuditReportDTO();
        dto.setWeakPasswordsCount(report.getWeakPasswordsCount());
        dto.setReusedPasswordsCount(report.getReusedPasswordsCount());
        dto.setOldPasswordsCount(report.getOldPasswordsCount());
        dto.setGeneratedAt(report.getGeneratedAt());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/questions/username/{username}")
    public ResponseEntity<List<String>> getSecurityQuestionsByUsername(@PathVariable String username) {
        Long userId = securityService.getUserIdByUsername(username);
        return ResponseEntity.ok(securityService.getSecurityQuestions(userId));
    }

    // Verify security answers
    @PostMapping("/verify-answers")
    public ResponseEntity<String> verifyAnswers(@RequestBody SecurityAnswerDTO dto) {
        boolean valid = securityService.verifyAnswers(dto);
        if (!valid) {
            return ResponseEntity.badRequest().body("Answers incorrect");
        }
        securityService.resetPassword(dto);
        return ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(
            @RequestParam Long userId,
            @RequestParam String code) {
        boolean valid = securityService.verifyCode(userId, code);
        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid code");
        }
        return ResponseEntity.ok("Verification successful");
    }
    
    @PostMapping("/verify-master-password")
    public ResponseEntity<String> verifyMasterPassword(
            @RequestParam String username,
            @RequestParam String password) {
        try {
            var user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return ResponseEntity.ok("Password verified");
            } else {
                return ResponseEntity.status(401).body("Incorrect password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Verification failed");
        }
    }
}