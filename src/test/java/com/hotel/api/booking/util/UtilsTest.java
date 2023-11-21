package com.hotel.api.booking.util;

import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    private Logger logger;

    @BeforeEach
    void setup() {
        logger = new Logger(this);
    }

    @Test
    void shouldLogWithoutException() {
        Executable log = () -> logger.log("test");
        Executable logWithLevel = () -> logger.log(Level.INFO, "test");
        Executable logException = () -> logger.logException(new RuntimeException());
        Executable logExceptionWithMsg = () -> logger.logException(new RuntimeException(), "test");
        Executable logExceptionWithLevel = () -> logger.logException(Level.INFO, new RuntimeException());

        assertAll(
                () -> assertDoesNotThrow(log),
                () -> assertDoesNotThrow(logWithLevel),
                () -> assertDoesNotThrow(logException),
                () -> assertDoesNotThrow(logExceptionWithMsg),
                () -> assertDoesNotThrow(logExceptionWithLevel)
        );
    }

    @Test
    void shouldMapObjectsCorrectly() {
        User expected = new User("test121", "admin@admin.com", "test123", Authority.USER);
        User actualUser = new User();

        GeneralUtils.map(expected, actualUser);

        assertEquals(expected, actualUser);
    }

    @Test
    void shouldNotMapIdIfSpecified() {
        User expected = new User("test121", "admin@admin.com", "test123", Authority.USER);
        expected.setId(12L);
        User actualUser = new User();

        GeneralUtils.map(expected, actualUser, false);

        assertEquals(expected.getName(), actualUser.getName());
        assertNull(actualUser.getId());
    }
}
