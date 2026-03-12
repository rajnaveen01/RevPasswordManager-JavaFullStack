package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.dto.LoginRequestDTO;
import com.revpasswordmanager.dto.RegisterRequestDTO;
import com.revpasswordmanager.entity.SecurityQuestion;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.repository.SecurityQuestionRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.service.AuthService;
import com.revpasswordmanager.service.SecurityService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl — Handles all authentication and account management logic.
 *
 * Log4j2 is used throughout this class to produce structured, searchable
 * audit-trail logs for every security-sensitive operation.
 */
@Service
public class AuthServiceImpl implements AuthService {

    // ── Logger ──────────────────────────────────────────────────────────────
    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    // ── Dependencies ────────────────────────────────────────────────────────
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityQuestionRepository securityQuestionRepository;
    private final SecurityService securityService;

    public AuthServiceImpl(UserRepository userRepository,
                           SecurityQuestionRepository securityQuestionRepository,
                           SecurityService securityService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityQuestionRepository = securityQuestionRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    // ── REGISTER USER ────────────────────────────────────────────────────────

    @Override
    public void registerUser(RegisterRequestDTO dto) {
        logger.info("Registration attempt — username: [{}], email: [{}]",
                dto.getUsername(), dto.getEmail());

        // Duplicate username check
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            logger.warn("Registration failed — username already exists: [{}]", dto.getUsername());
            throw new RuntimeException(
                "Username \"" + dto.getUsername() + "\" is already taken. Please choose a different username.");
        }

        // Duplicate email check
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Registration failed — email already exists: [{}]", dto.getEmail());
            throw new RuntimeException(
                "An account with email \"" + dto.getEmail() + "\" already exists.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(dto.getMasterPassword()));

        User savedUser = userRepository.save(user);
        logger.debug("User entity persisted — id: [{}], username: [{}]",
                savedUser.getId(), savedUser.getUsername());

        // Save 3 security questions
        saveSecurityQuestion(dto.getQuestion1(), dto.getAnswer1(), savedUser, 1);
        saveSecurityQuestion(dto.getQuestion2(), dto.getAnswer2(), savedUser, 2);
        saveSecurityQuestion(dto.getQuestion3(), dto.getAnswer3(), savedUser, 3);

        logger.info("Registration successful — userId: [{}], username: [{}]",
                savedUser.getId(), savedUser.getUsername());
    }

    private void saveSecurityQuestion(String question, String answer, User user, int index) {
        SecurityQuestion sq = new SecurityQuestion();
        sq.setQuestion(question);
        sq.setAnswerHash(passwordEncoder.encode(answer));
        sq.setUser(user);
        securityQuestionRepository.save(sq);
        logger.debug("Security question {} saved for userId: [{}]", index, user.getId());
    }

    // ── LOGIN ────────────────────────────────────────────────────────────────

    @Override
    public Long login(LoginRequestDTO dto) {
        logger.info("Login attempt — username: [{}]", dto.getUsername());

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed — user not found: [{}]", dto.getUsername());
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            logger.warn("Login failed — invalid password for username: [{}]", dto.getUsername());
            throw new RuntimeException("Invalid password");
        }

        // 2FA check
        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            logger.info("2FA required — generating verification code for userId: [{}]", user.getId());
            securityService.generateVerificationCode(user.getId(), "LOGIN");
            return -1L; // Signal to frontend: 2FA step needed
        }

        logger.info("Login successful — userId: [{}], username: [{}]", user.getId(), user.getUsername());
        return user.getId();
    }

    // ── CHANGE MASTER PASSWORD ───────────────────────────────────────────────

    @Override
    public void changeMasterPassword(String username, String currentPassword, String newPassword) {
        logger.info("Change master password request — username: [{}]", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Change password failed — user not found: [{}]", username);
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            logger.warn("Change password failed — current password incorrect for username: [{}]", username);
            throw new RuntimeException("Current password incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Master password changed successfully — username: [{}]", username);
    }

    // ── TOGGLE 2FA ───────────────────────────────────────────────────────────

    @Override
    public void toggleTwoFactor(String username, Boolean enabled) {
        logger.info("Toggle 2FA request — username: [{}], enable: [{}]", username, enabled);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Toggle 2FA failed — user not found: [{}]", username);
                    return new RuntimeException("User not found");
                });

        user.setTwoFactorEnabled(enabled);
        userRepository.save(user);

        logger.info("2FA status updated — username: [{}], twoFactorEnabled: [{}]", username, enabled);
    }

    // ── PASSWORD RECOVERY ────────────────────────────────────────────────────

    @Override
    public void recoverPassword(String username) {
        logger.info("Password recovery initiated for username: [{}]", username);
        // Full implementation handled by SecurityController / SecurityService
    }

    // ── LOGOUT ───────────────────────────────────────────────────────────────

    @Override
    public void logout() {
        // Session invalidation is handled by Spring Security (SecurityConfig).
        // This method is a hook for any post-logout business logic (e.g., audit log).
        logger.info("Logout operation triggered");
    }
}