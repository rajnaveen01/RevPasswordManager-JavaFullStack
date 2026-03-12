package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.PasswordGeneratorRequestDTO;
import com.revpasswordmanager.serviceimpl.PasswordGeneratorServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PasswordGeneratorServiceImplTest {

    private static final Logger logger = LogManager.getLogger(PasswordGeneratorServiceImplTest.class);

    private PasswordGeneratorServiceImpl generatorService;

    @Before
    public void setUp() {
        logger.info("=== Setting up PasswordGeneratorServiceImplTest ===");
        generatorService = new PasswordGeneratorServiceImpl();
    }

    private PasswordGeneratorRequestDTO allEnabled(int length) {
        PasswordGeneratorRequestDTO dto = new PasswordGeneratorRequestDTO();
        dto.setLength(length);
        dto.setIncludeUppercase(true);
        dto.setIncludeLowercase(true);
        dto.setIncludeNumbers(true);
        dto.setIncludeSpecialChars(true);
        dto.setExcludeSimilarChars(false);
        return dto;
    }

    @Test
    public void generatePassword_length12_exactLength() {
        logger.info("TEST: generatePassword_length12_exactLength");
        String p = generatorService.generatePassword(allEnabled(12));
        assertEquals(12, p.length());
        logger.info("PASS: length={}", p.length());
    }

    @Test
    public void generatePassword_length8_exactLength() {
        logger.info("TEST: generatePassword_length8_exactLength");
        assertEquals(8, generatorService.generatePassword(allEnabled(8)).length());
        logger.info("PASS");
    }

    @Test
    public void generatePassword_length64_exactLength() {
        logger.info("TEST: generatePassword_length64_exactLength");
        assertEquals(64, generatorService.generatePassword(allEnabled(64)).length());
        logger.info("PASS");
    }

    @Test
    public void generatePassword_uppercaseOnly_onlyUppercase() {
        logger.info("TEST: generatePassword_uppercaseOnly_onlyUppercase");
        PasswordGeneratorRequestDTO dto = new PasswordGeneratorRequestDTO();
        dto.setLength(20);
        dto.setIncludeUppercase(true);
        dto.setIncludeLowercase(false);
        dto.setIncludeNumbers(false);
        dto.setIncludeSpecialChars(false);
        String p = generatorService.generatePassword(dto);
        assertTrue(p.matches("[A-Z]+"));
        logger.info("PASS: password={}", p);
    }

    @Test
    public void generatePassword_numbersOnly_onlyDigits() {
        logger.info("TEST: generatePassword_numbersOnly_onlyDigits");
        PasswordGeneratorRequestDTO dto = new PasswordGeneratorRequestDTO();
        dto.setLength(16);
        dto.setIncludeUppercase(false);
        dto.setIncludeLowercase(false);
        dto.setIncludeNumbers(true);
        dto.setIncludeSpecialChars(false);
        String p = generatorService.generatePassword(dto);
        assertTrue(p.matches("[0-9]+"));
        logger.info("PASS");
    }

    @Test
    public void generatePassword_excludeSimilarChars_nonePresent() {
        logger.info("TEST: generatePassword_excludeSimilarChars_nonePresent");
        PasswordGeneratorRequestDTO dto = allEnabled(100);
        dto.setExcludeSimilarChars(true);
        for (int i = 0; i < 10; i++) {
            String p = generatorService.generatePassword(dto);
            assertFalse(p.contains("0"));
            assertFalse(p.contains("O"));
            assertFalse(p.contains("l"));
            assertFalse(p.contains("1"));
            assertFalse(p.contains("I"));
        }
        logger.info("PASS: no similar chars in 10 passwords");
    }

    @Test(expected = RuntimeException.class)
    public void generatePassword_noTypesSelected_throwsException() {
        logger.info("TEST: generatePassword_noTypesSelected_throwsException");
        PasswordGeneratorRequestDTO dto = new PasswordGeneratorRequestDTO();
        dto.setLength(12);
        dto.setIncludeUppercase(false);
        dto.setIncludeLowercase(false);
        dto.setIncludeNumbers(false);
        dto.setIncludeSpecialChars(false);
        generatorService.generatePassword(dto);
    }

    @Test
    public void generatePassword_twoCalls_differentPasswords() {
        logger.info("TEST: generatePassword_twoCalls_differentPasswords");
        String p1 = generatorService.generatePassword(allEnabled(20));
        String p2 = generatorService.generatePassword(allEnabled(20));
        assertNotEquals(p1, p2);
        logger.info("PASS");
    }

    @Test
    public void generateMultiple_count5_returnsFive() {
        logger.info("TEST: generateMultiple_count5_returnsFive");
        List<String> list = generatorService.generateMultiplePasswords(allEnabled(16), 5);
        assertNotNull(list);
        assertEquals(5, list.size());
        logger.info("PASS");
    }

    @Test
    public void generateMultiple_allUnique() {
        logger.info("TEST: generateMultiple_allUnique");
        List<String> list = generatorService.generateMultiplePasswords(allEnabled(20), 10);
        Set<String> unique = new HashSet<>(list);
        assertEquals(10, unique.size());
        logger.info("PASS");
    }

    @Test
    public void checkStrength_simplePassword_weak() {
        logger.info("TEST: checkStrength_simplePassword_weak");
        assertEquals("Weak", generatorService.checkStrength("abc"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_medium_returnsMediumOrStrong() {
        logger.info("TEST: checkStrength_medium_returnsMediumOrStrong");
        String s = generatorService.checkStrength("Hello123");
        assertTrue(s.equals("Medium") || s.equals("Strong"));
        logger.info("PASS: strength={}", s);
    }

    @Test
    public void checkStrength_complexLong_strongOrVeryStrong() {
        logger.info("TEST: checkStrength_complexLong_strongOrVeryStrong");
        String s = generatorService.checkStrength("SecurePass@123!");
        assertTrue(s.equals("Strong") || s.equals("Very Strong"));
        logger.info("PASS: strength={}", s);
    }

    @Test
    public void checkStrength_null_returnsWeak() {
        logger.info("TEST: checkStrength_null_returnsWeak");
        assertEquals("Weak", generatorService.checkStrength(null));
        logger.info("PASS");
    }
}