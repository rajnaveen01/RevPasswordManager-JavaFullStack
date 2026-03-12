package com.revpasswordmanager.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class EncryptionUtilTest {

    private static final Logger logger = LogManager.getLogger(EncryptionUtilTest.class);

    @Test
    public void encrypt_validInput_notNullNotEmpty() {
        logger.info("TEST: encrypt returns non-null, non-empty");
        String enc = EncryptionUtil.encrypt("Hello World!");
        assertNotNull(enc);
        assertFalse(enc.isEmpty());
        logger.info("PASS: encrypted={}", enc);
    }

    @Test
    public void encrypt_notSameAsPlaintext() {
        logger.info("TEST: ciphertext differs from plaintext");
        String plain = "MySecret@123";
        assertNotEquals(plain, EncryptionUtil.encrypt(plain));
        logger.info("PASS");
    }

    @Test
    public void encrypt_outputIsBase64() {
        logger.info("TEST: output is Base64");
        String enc = EncryptionUtil.encrypt("TestPassword");
        assertTrue(enc.matches("^[A-Za-z0-9+/=]+$"));
        logger.info("PASS");
    }

    @Test
    public void decrypt_roundTrip_restoresOriginal() {
        logger.info("TEST: encrypt→decrypt round trip");
        String original = "MySecretPassword@1";
        assertEquals(original, EncryptionUtil.decrypt(EncryptionUtil.encrypt(original)));
        logger.info("PASS");
    }

    @Test
    public void decrypt_specialChars_roundTrip() {
        logger.info("TEST: special chars round trip");
        String original = "P@$$w0rd!#%^&*";
        assertEquals(original, EncryptionUtil.decrypt(EncryptionUtil.encrypt(original)));
        logger.info("PASS");
    }

    @Test
    public void decrypt_numericPassword_roundTrip() {
        logger.info("TEST: numeric password round trip");
        String original = "1234567890";
        assertEquals(original, EncryptionUtil.decrypt(EncryptionUtil.encrypt(original)));
        logger.info("PASS");
    }

    @Test
    public void decrypt_longPassword_roundTrip() {
        logger.info("TEST: long password round trip");
        String original = "VeryLongPassword@1234567890WithExtraChars!";
        assertEquals(original, EncryptionUtil.decrypt(EncryptionUtil.encrypt(original)));
        logger.info("PASS");
    }

    @Test
    public void encrypt_sameInputTwice_sameCiphertext() {
        logger.info("TEST: same input → same ciphertext (ECB determinism)");
        String input = "ConsistentInput";
        assertEquals(EncryptionUtil.encrypt(input), EncryptionUtil.encrypt(input));
        logger.info("PASS");
    }

    @Test
    public void encrypt_differentInputs_differentCiphertexts() {
        logger.info("TEST: different inputs → different ciphertexts");
        assertNotEquals(EncryptionUtil.encrypt("Pass1"), EncryptionUtil.encrypt("Pass2"));
        logger.info("PASS");
    }
}