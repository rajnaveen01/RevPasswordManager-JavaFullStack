package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.LoginRequestDTO;
import com.revpasswordmanager.dto.RegisterRequestDTO;
import com.revpasswordmanager.entity.SecurityQuestion;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.repository.SecurityQuestionRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.serviceimpl.AuthServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    private static final Logger logger = LogManager.getLogger(AuthServiceImplTest.class);

    @Mock private UserRepository             userRepository;
    @Mock private SecurityQuestionRepository securityQuestionRepository;
    @Mock private SecurityService            securityService;
    @Mock private PasswordEncoder            passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDTO validDTO;
    private User               testUser;

    @Before
    public void setUp() {
        logger.info("=== Setting up AuthServiceImplTest ===");

        validDTO = new RegisterRequestDTO();
        validDTO.setUsername("john_doe");
        validDTO.setEmail("john@example.com");
        validDTO.setMasterPassword("SecurePass@1");
        validDTO.setPhoneNumber("9876543210");
        validDTO.setQuestion1("Pet name?");   validDTO.setAnswer1("Buddy");
        validDTO.setQuestion2("Birth city?"); validDTO.setAnswer2("Chennai");
        validDTO.setQuestion3("Mom name?");   validDTO.setAnswer3("Sharma");

        testUser = new User();
        testUser.setId(100L);              // ✅ FIX 1: give testUser an ID so login() returns 100L
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPasswordHash("$2a$hashed");
        testUser.setTwoFactorEnabled(false);
    }

    // ── registerUser ──────────────────────────────────────────────

    @Test
    public void registerUser_validData_savesUserAndQuestions() {
        logger.info("TEST: registerUser_validData_savesUserAndQuestions");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");

        // ✅ FIX 2: AuthServiceImpl does User savedUser = userRepository.save(user)
        //    then calls q1.setUser(savedUser). Mockito returns null by default.
        //    Mock save() to return testUser so savedUser is not null.
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(securityQuestionRepository.save(any(SecurityQuestion.class)))
                .thenAnswer(i -> i.getArgument(0));

        authService.registerUser(validDTO);

        verify(userRepository, times(1)).save(any(User.class));
        verify(securityQuestionRepository, times(3)).save(any(SecurityQuestion.class));
        logger.info("PASS");
    }

    @Test(expected = RuntimeException.class)
    public void registerUser_duplicateUsername_throwsException() {
        logger.info("TEST: registerUser_duplicateUsername_throwsException");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        authService.registerUser(validDTO);
    }

    @Test(expected = RuntimeException.class)
    public void registerUser_duplicateEmail_throwsException() {
        logger.info("TEST: registerUser_duplicateEmail_throwsException");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        authService.registerUser(validDTO);
    }

    @Test
    public void registerUser_passwordIsEncoded_notPlaintext() {
        logger.info("TEST: registerUser_passwordIsEncoded_notPlaintext");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(securityQuestionRepository.save(any(SecurityQuestion.class)))
                .thenAnswer(i -> i.getArgument(0));

        authService.registerUser(validDTO);

        // 1 master password + 3 security answers = 4 encode calls
        verify(passwordEncoder, times(4)).encode(anyString());
        logger.info("PASS");
    }

    // ── login ─────────────────────────────────────────────────────

    @Test
    public void login_validCredentials_no2FA_returnsUserId() {
        logger.info("TEST: login_validCredentials_no2FA_returnsUserId");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("john_doe");
        dto.setPassword("SecurePass@1");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SecurePass@1", "$2a$hashed")).thenReturn(true);

        Long result = authService.login(dto);

        // testUser.getId() returns 100L (set in setUp)
        // login() returns user.getId() when no 2FA → result should be 100L
        assertNotNull("Login should return a non-null userId", result);
        assertEquals((long) 100L, (long) result);
        logger.info("PASS: userId={}", result);
    }

    @Test
    public void login_validCredentials_with2FA_returnsNegativeOne() {
        logger.info("TEST: login_validCredentials_with2FA_returnsNegativeOne");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("john_doe");
        dto.setPassword("SecurePass@1");

        testUser.setTwoFactorEnabled(true);
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SecurePass@1", "$2a$hashed")).thenReturn(true);

        Long result = authService.login(dto);

        assertEquals(-1L, result.longValue());
        verify(securityService, times(1)).generateVerificationCode(any(), eq("LOGIN"));
        logger.info("PASS: 2FA triggered, returned -1");
    }

    @Test(expected = RuntimeException.class)
    public void login_userNotFound_throwsException() {
        logger.info("TEST: login_userNotFound_throwsException");
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("ghost");
        dto.setPassword("any");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        authService.login(dto);
    }

    @Test(expected = RuntimeException.class)
    public void login_wrongPassword_throwsException() {
        logger.info("TEST: login_wrongPassword_throwsException");
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("john_doe");
        dto.setPassword("WrongPass");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPass", "$2a$hashed")).thenReturn(false);
        authService.login(dto);
    }

    // ── changeMasterPassword ──────────────────────────────────────

    @Test
    public void changeMasterPassword_correctCurrent_updatesHash() {
        logger.info("TEST: changeMasterPassword_correctCurrent_updatesHash");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("OldPass@1", "$2a$hashed")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@2")).thenReturn("$2a$new");

        authService.changeMasterPassword("john_doe", "OldPass@1", "NewPass@2");

        verify(userRepository, times(1)).save(testUser);
        assertEquals("$2a$new", testUser.getPasswordHash());
        logger.info("PASS");
    }

    @Test(expected = RuntimeException.class)
    public void changeMasterPassword_wrongCurrent_throwsException() {
        logger.info("TEST: changeMasterPassword_wrongCurrent_throwsException");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Wrong", "$2a$hashed")).thenReturn(false);
        authService.changeMasterPassword("john_doe", "Wrong", "NewPass@2");
    }

    // ── toggleTwoFactor ───────────────────────────────────────────

    @Test
    public void toggleTwoFactor_enable_setsTrue() {
        logger.info("TEST: toggleTwoFactor_enable_setsTrue");
        testUser.setTwoFactorEnabled(false);
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));

        authService.toggleTwoFactor("john_doe", true);

        assertTrue(testUser.getTwoFactorEnabled());
        verify(userRepository, times(1)).save(testUser);
        logger.info("PASS");
    }

    @Test
    public void toggleTwoFactor_disable_setsFalse() {
        logger.info("TEST: toggleTwoFactor_disable_setsFalse");
        testUser.setTwoFactorEnabled(true);
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));

        authService.toggleTwoFactor("john_doe", false);

        assertFalse(testUser.getTwoFactorEnabled());
        verify(userRepository, times(1)).save(testUser);
        logger.info("PASS");
    }
}