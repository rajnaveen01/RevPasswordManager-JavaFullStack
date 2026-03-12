package com.revpasswordmanager;

import com.revpasswordmanager.controller.SecurityControllerTest;
import com.revpasswordmanager.service.AuthServiceImplTest;
import com.revpasswordmanager.service.PasswordGeneratorServiceImplTest;
import com.revpasswordmanager.service.VaultServiceImplTest;
import com.revpasswordmanager.util.EncryptionUtilTest;
import com.revpasswordmanager.util.PasswordStrengthUtilTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * RevPasswordManagerTestSuite — runs ALL JUnit 4 tests in one command.
 *
 * Usage:
 *   mvn test                              (runs everything)
 *   mvn test -Dtest=RevPasswordManagerTestSuite   (suite only)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AuthServiceImplTest.class,
    VaultServiceImplTest.class,
    PasswordGeneratorServiceImplTest.class,
    SecurityControllerTest.class,
    PasswordStrengthUtilTest.class,
    EncryptionUtilTest.class
})
public class RevPasswordManagerTestSuite {

    private static final Logger logger = LogManager.getLogger(RevPasswordManagerTestSuite.class);

    @BeforeClass
    public static void suiteStarted() {
        logger.info("╔══════════════════════════════════════════════╗");
        logger.info("║  RevPasswordManager Test Suite  — STARTING  ║");
        logger.info("╚══════════════════════════════════════════════╝");
    }

    @AfterClass
    public static void suiteFinished() {
        logger.info("╔══════════════════════════════════════════════╗");
        logger.info("║  RevPasswordManager Test Suite  — COMPLETE  ║");
        logger.info("╚══════════════════════════════════════════════╝");
    }
}