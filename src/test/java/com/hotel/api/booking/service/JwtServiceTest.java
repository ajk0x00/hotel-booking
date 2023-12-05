package com.hotel.api.booking.service;

import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@SpringBootTest
public class JwtServiceTest {

    private JwtUtil jwtService;

    @BeforeEach
    public void instantiate() {
        jwtService = new JwtUtil("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpYXQiOjE3MDA3OTk0ODAsImV4cCI6MTcwMDk3MjI4MH0.uKVRrWxaYIvndPM0V4OKPUhvg1-5QwonSB9dI_aWfuA");
    }

    @Test
    public void shouldExtractCorrectEmail() {
        String expectedEmail = "admin@admin.com";

        String actualEmail = jwtService.extractUsername();

        assertEquals("Actual and extracted emails should be equal", expectedEmail, actualEmail);
    }

    @Test
    public void shouldGenerateCorrectToken() {
        User user = new User("test121", "test@admin.com", "test123", Authority.USER);

        String token = JwtUtil.generateToken(user);
        JwtUtil service = new JwtUtil(token);
        String emailInToken = service.extractUsername();

        assertEquals("Email in token should be same as email in User",
                user.getEmail(), emailInToken);
    }

    @Test
    public void shouldThrowExceptionWhenTokenIsMalformed() {
        Executable executable = () -> new JwtUtil("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZsadG1pbkBhZG1pbi5jb20iLCJpYXQiOjE3MDA1NTE4NDgsImV4cCI6MTcwMDcyNDY0OH0.dxYp9z8fkU_KguMbT8dIelOMEBNMk1s65cG2x5rjzr0");

        assertThrows(SignatureException.class, executable);
    }

    @Test
    public void shouldReturnTrueForValidToken() {
        User user = new User("test121", "admin@admin.com", "test123", Authority.USER);

        boolean isValid = jwtService.isTokenValid(user);

        assertTrue(isValid);
    }

    @Test
    public void shouldNotReturnTrueForInValidToken() {
        User user = new User("test121", "test@admin.com", "test123", Authority.USER);

        System.out.println(jwtService.extractUsername());
        boolean isValid = jwtService.isTokenValid(user);

        assertFalse("Invalid token for user should return false", isValid);
    }

    @Test
    public void shouldThrowExceptionForExpiredToken() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpYXQiOjE3MDA1NTE4NDgsImV4cCI6MTcwMDU1MTg0OH0.guKlzvEucHYYC66j99jN8ixSDxIPoWDzlcg--2cQYcs";
        Executable executable = () -> new JwtUtil(expiredToken);

        assertThrows(ExpiredJwtException.class, executable);
    }
}
