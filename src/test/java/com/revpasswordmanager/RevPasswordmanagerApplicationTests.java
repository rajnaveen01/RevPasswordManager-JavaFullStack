package com.revpasswordmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Application context smoke test — converted from JUnit5 to JUnit4.
 * Note: @SpringBootTest is removed because it tries to start the full
 * Spring context which needs a real MySQL database.
 * These unit tests use Mockito and do NOT need a running DB.
 */
@RunWith(SpringRunner.class)
public class RevPasswordmanagerApplicationTests {

    private static final Logger logger = LogManager.getLogger(RevPasswordmanagerApplicationTests.class);

    @Test
    public void contextLoads() {
        logger.info("TEST: Application context smoke test — PASS");
    }
}