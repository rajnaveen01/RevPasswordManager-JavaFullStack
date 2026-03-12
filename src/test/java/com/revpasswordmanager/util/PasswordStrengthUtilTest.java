package com.revpasswordmanager.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PasswordStrengthUtilTest {

    private static final Logger logger = LogManager.getLogger(PasswordStrengthUtilTest.class);

    @Test
    public void checkStrength_null_returnsWeak() {
        logger.info("TEST: null → Weak");
        assertEquals("Weak", PasswordStrengthUtil.checkStrength(null));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_empty_returnsWeak() {
        logger.info("TEST: empty → Weak");
        assertEquals("Weak", PasswordStrengthUtil.checkStrength(""));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_abc_returnsWeak() {
        logger.info("TEST: 'abc' → Weak");
        assertEquals("Weak", PasswordStrengthUtil.checkStrength("abc"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_eightLowerOnly_returnsWeak() {
        logger.info("TEST: 'abcdefgh' → Weak");
        assertEquals("Weak", PasswordStrengthUtil.checkStrength("abcdefgh"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_upperLowerLength8_returnsMedium() {
        logger.info("TEST: 'HelloWld' → Medium");
        assertEquals("Medium", PasswordStrengthUtil.checkStrength("HelloWld"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_upperLowerDigit_returnsMedium() {
        logger.info("TEST: 'Hello123' → Medium");
        assertEquals("Medium", PasswordStrengthUtil.checkStrength("Hello123"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_upperLowerDigitSpecial8_returnsStrong() {
        logger.info("TEST: 'Hello@12' → Strong");
        assertEquals("Strong", PasswordStrengthUtil.checkStrength("Hello@12"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_allCriteria12chars_returnsVeryStrong() {
        logger.info("TEST: 'SecurePass@123' → Very Strong");
        assertEquals("Very Strong", PasswordStrengthUtil.checkStrength("SecurePass@123"));
        logger.info("PASS");
    }

    @Test
    public void checkStrength_maxComplexity_returnsVeryStrong() {
        logger.info("TEST: long complex password → Very Strong");
        assertEquals("Very Strong", PasswordStrengthUtil.checkStrength("Tr0ub4dor&3xYzSecure!"));
        logger.info("PASS");
    }
}