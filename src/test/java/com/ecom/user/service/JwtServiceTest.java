package com.ecom.user.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-that-is-long-enough-for-hs256", 3600000);
    }

    @Test
    void generatesToken() {
        String token = jwtService.generateToken("user-123", "test@email.com");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void parsesToken() {
        String token = jwtService.generateToken("user-123", "test@email.com");
        Claims claims = jwtService.parseToken(token);
        assertEquals("user-123", claims.getSubject());
        assertEquals("test@email.com", claims.get("email"));
    }

    @Test
    void rejectsInvalidToken() {
        assertThrows(Exception.class, () -> jwtService.parseToken("invalid-token"));
    }
}
