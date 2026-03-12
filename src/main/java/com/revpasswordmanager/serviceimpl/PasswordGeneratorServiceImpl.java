package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.dto.PasswordGeneratorRequestDTO;
import com.revpasswordmanager.service.PasswordGeneratorService;
import com.revpasswordmanager.util.PasswordStrengthUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * PasswordGeneratorServiceImpl — Generates cryptographically strong passwords.
 *
 * Uses SecureRandom (not java.util.Random) for all generation to ensure
 * passwords are unpredictable.
 */
@Service
public class PasswordGeneratorServiceImpl implements PasswordGeneratorService {

    private static final Logger logger = LogManager.getLogger(PasswordGeneratorServiceImpl.class);

    private static final String UPPER           = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER           = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS         = "0123456789";
    private static final String SPECIAL         = "!@#$%^&*()_+";
    private static final String SIMILAR_CHARS   = "0Ol1iI|";   // excluded when flag is set

    private final SecureRandom random = new SecureRandom();

    // ── GENERATE SINGLE PASSWORD ─────────────────────────────────────────────

    @Override
    public String generatePassword(PasswordGeneratorRequestDTO dto) {
        logger.debug("Generating password — length: [{}], upper: [{}], lower: [{}], " +
                     "numbers: [{}], special: [{}], excludeSimilar: [{}]",
                dto.getLength(), dto.isIncludeUppercase(), dto.isIncludeLowercase(),
                dto.isIncludeNumbers(), dto.isIncludeSpecialChars(), dto.isExcludeSimilarChars());

        StringBuilder characters = new StringBuilder();

        if (dto.isIncludeUppercase())    characters.append(UPPER);
        if (dto.isIncludeLowercase())    characters.append(LOWER);
        if (dto.isIncludeNumbers())      characters.append(NUMBERS);
        if (dto.isIncludeSpecialChars()) characters.append(SPECIAL);

        if (characters.length() == 0) {
            logger.warn("Password generation failed — no character types selected");
            throw new RuntimeException("No character types selected. Please enable at least one option.");
        }

        // Optionally strip similar-looking characters
        String pool = characters.toString();
        if (dto.isExcludeSimilarChars()) {
            StringBuilder filtered = new StringBuilder();
            for (char c : pool.toCharArray()) {
                if (SIMILAR_CHARS.indexOf(c) == -1) filtered.append(c);
            }
            pool = filtered.toString();
            logger.debug("Similar characters excluded — remaining pool size: [{}]", pool.length());
        }

        if (pool.isEmpty()) {
            logger.warn("Password generation failed — pool empty after exclusions");
            throw new RuntimeException("No characters remain after applying exclusions.");
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < dto.getLength(); i++) {
            password.append(pool.charAt(random.nextInt(pool.length())));
        }

        String result = password.toString();
        String strength = PasswordStrengthUtil.checkStrength(result);
        logger.info("Password generated — length: [{}], strength: [{}]", result.length(), strength);

        return result;
    }

    // ── GENERATE MULTIPLE PASSWORDS ──────────────────────────────────────────

    @Override
    public List<String> generateMultiplePasswords(PasswordGeneratorRequestDTO dto, int count) {
        logger.info("Generating [{}] passwords — length: [{}]", count, dto.getLength());

        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generatePassword(dto));
        }

        logger.debug("Bulk generation complete — [{}] passwords generated", passwords.size());
        return passwords;
    }

    // ── CHECK PASSWORD STRENGTH ──────────────────────────────────────────────

    @Override
    public String checkStrength(String password) {
        String strength = PasswordStrengthUtil.checkStrength(password);
        logger.debug("Strength check — result: [{}], length: [{}]", strength, password == null ? 0 : password.length());
        return strength;
    }
}