package com.revpasswordmanager.controller;

import com.revpasswordmanager.dto.AuditReportDTO;
import com.revpasswordmanager.dto.SecurityAnswerDTO;
import com.revpasswordmanager.dto.SecurityQuestionDTO;
import com.revpasswordmanager.entity.SecurityAuditReport;
import com.revpasswordmanager.entity.SecurityQuestion;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.service.AuditService;
import com.revpasswordmanager.service.SecurityService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * SecurityControllerTest — JUnit 4
 *
 * ROOT CAUSE OF "assertEquals is ambiguous":
 *   AuditReportDTO.getWeakPasswordsCount() returns primitive int (NOT Integer).
 *   When you write:
 *       assertEquals(Integer.valueOf(3), response.getBody().getWeakPasswordsCount())
 *   Java sees:
 *       expected = Integer  (Object)
 *       actual   = int      → auto-boxes to Integer (Object)
 *   Both assertEquals(long,long) and assertEquals(Object,Object) still match → AMBIGUOUS.
 *
 * DEFINITIVE FIX:
 *   Cast BOTH sides to (long):
 *       assertEquals((long) 3, (long) response.getBody().getWeakPasswordsCount())
 *   This forces Java to pick assertEquals(long, long) — only ONE overload matches.
 *   No ambiguity. No auto-boxing. Compiles cleanly every time.
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityControllerTest {

    private static final Logger logger = LogManager.getLogger(SecurityControllerTest.class);

    @Mock private SecurityService securityService;
    @Mock private AuditService    auditService;

    @InjectMocks
    private SecurityController securityController;

    @Before
    public void setUp() {
        logger.info("=== Setting up SecurityControllerTest ===");
    }

    // ════════════════════════════════════════════════════════════════
    //  GET /api/security/questions/{userId}
    // ════════════════════════════════════════════════════════════════

    @Test
    public void getSecurityQuestions_validUserId_returnsOkWithQuestions() {
        logger.info("TEST: getSecurityQuestions_validUserId_returnsOkWithQuestions");

        List<String> questions = Arrays.asList(
                "What is your pet's name?",
                "What city were you born in?",
                "What is your mother's maiden name?"
        );
        when(securityService.getSecurityQuestions(1L)).thenReturn(questions);

        ResponseEntity<List<String>> response = securityController.getSecurityQuestions(1L);

        // HttpStatus vs HttpStatus → Object, Object → fine
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // int vs int → cast both to (long) → assertEquals(long, long) → unambiguous
        assertEquals((long) 3, (long) response.getBody().size());

        logger.info("PASS: {} questions returned", response.getBody().size());
    }

    // ════════════════════════════════════════════════════════════════
    //  POST /api/security/verification/{userId}
    // ════════════════════════════════════════════════════════════════

    @Test
    public void generateVerificationCode_validRequest_returnsCode() {
        logger.info("TEST: generateVerificationCode_validRequest_returnsCode");

        when(securityService.generateVerificationCode(1L, "LOGIN")).thenReturn("482931");

        ResponseEntity<String> response = securityController.generateVerificationCode(1L, "LOGIN");

        // String vs String → Object, Object → fine
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue("Body should contain the code", response.getBody().contains("482931"));

        logger.info("PASS: code returned in body");
    }

    // ════════════════════════════════════════════════════════════════
    //  GET /api/security/audit/{userId}
    // ════════════════════════════════════════════════════════════════

    @Test
    public void getSecurityAudit_validUserId_returnsAuditReport() {
        logger.info("TEST: getSecurityAudit_validUserId_returnsAuditReport");

        SecurityAuditReport report = new SecurityAuditReport();
        report.setWeakPasswordsCount(3);
        report.setReusedPasswordsCount(1);
        report.setOldPasswordsCount(2);
        report.setGeneratedAt(LocalDateTime.now());

        when(auditService.generateAuditReport(1L)).thenReturn(report);

        ResponseEntity<AuditReportDTO> response = securityController.getSecurityAudit(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // ✅ DEFINITIVE FIX:
        //    AuditReportDTO.getWeakPasswordsCount() returns primitive int.
        //    Cast BOTH sides to (long) → forces assertEquals(long, long) → zero ambiguity.
        assertEquals((long) 3, (long) response.getBody().getWeakPasswordsCount());
        assertEquals((long) 1, (long) response.getBody().getReusedPasswordsCount());
        assertEquals((long) 2, (long) response.getBody().getOldPasswordsCount());

        logger.info("PASS: weak={}, reused={}, old={}",
                response.getBody().getWeakPasswordsCount(),
                response.getBody().getReusedPasswordsCount(),
                response.getBody().getOldPasswordsCount());
    }

    // ════════════════════════════════════════════════════════════════
    //  POST /api/security/verify-code  (valid)
    // ════════════════════════════════════════════════════════════════

    @Test
    public void verifyCode_validCode_returns200() {
        logger.info("TEST: verifyCode_validCode_returns200");

        when(securityService.verifyCode(1L, "123456")).thenReturn(true);

        ResponseEntity<String> response = securityController.verifyCode(1L, "123456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Verification successful", response.getBody());

        logger.info("PASS");
    }

    // ════════════════════════════════════════════════════════════════
    //  POST /api/security/verify-code  (invalid)
    // ════════════════════════════════════════════════════════════════

    @Test
    public void verifyCode_invalidCode_returns400() {
        logger.info("TEST: verifyCode_invalidCode_returns400");

        when(securityService.verifyCode(1L, "000000")).thenReturn(false);

        ResponseEntity<String> response = securityController.verifyCode(1L, "000000");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid code", response.getBody());

        logger.info("PASS");
    }

    // ════════════════════════════════════════════════════════════════
    //  PUT /api/security/question/{questionId}  (valid answer)
    // ════════════════════════════════════════════════════════════════

    @Test
    public void updateSecurityAnswer_validInput_returns200() {
        logger.info("TEST: updateSecurityAnswer_validInput_returns200");

        SecurityQuestionDTO dto = new SecurityQuestionDTO();
        dto.setAnswer("NewAnswer");
        doNothing().when(securityService).updateSecurityAnswer(1L, "NewAnswer");

        ResponseEntity<String> response = securityController.updateSecurityAnswer(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Security answer updated successfully", response.getBody());

        logger.info("PASS");
    }

    // ════════════════════════════════════════════════════════════════
    //  PUT /api/security/question/{questionId}  (empty answer)
    // ════════════════════════════════════════════════════════════════

    @Test
    public void updateSecurityAnswer_emptyAnswer_returns400() {
        logger.info("TEST: updateSecurityAnswer_emptyAnswer_returns400");

        SecurityQuestionDTO dto = new SecurityQuestionDTO();
        dto.setAnswer("");
        doThrow(new IllegalArgumentException("Answer cannot be empty."))
                .when(securityService).updateSecurityAnswer(1L, "");

        ResponseEntity<String> response = securityController.updateSecurityAnswer(1L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue("Body should mention 'empty'", response.getBody().contains("empty"));

        logger.info("PASS");
    }
    
}