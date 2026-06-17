package com.ecom.user.service;

import com.ecom.user.dto.RefreshTokenResponse;
import com.ecom.user.model.RefreshToken;
import com.ecom.user.model.User;
import com.ecom.user.repository.RefreshTokenRepository;
import com.ecom.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository, jwtService, userRepository);
    }

    @Test
    void createRefreshTokenSavesAndReturns() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken rt = service.createRefreshToken("user-1");

        assertNotNull(rt.getToken());
        assertEquals("user-1", rt.getUserId());
        assertFalse(rt.isRevoked());
        assertNotNull(rt.getExpiresAt());
        assertTrue(rt.getExpiresAt().isAfter(LocalDateTime.now()));
        verify(repository).save(any());
    }

    @Test
    void refreshAccessTokenReturnsNewTokens() {
        String oldTokenStr = UUID.randomUUID().toString();
        User user = new User();
        user.setId("user-1");
        user.setEmail("test@email.com");

        RefreshToken stored = new RefreshToken();
        stored.setId("rt-1");
        stored.setUserId("user-1");
        stored.setToken(oldTokenStr);
        stored.setExpiresAt(LocalDateTime.now().plusDays(5));
        stored.setRevoked(false);

        when(repository.findByToken(oldTokenStr)).thenReturn(Optional.of(stored));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user-1", "test@email.com")).thenReturn("new-jwt");
        when(jwtService.getExpiration()).thenReturn(3600000L);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenResponse response = service.refreshAccessToken(oldTokenStr);

        assertEquals("new-jwt", response.getToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(3600000L, response.getExpiresIn());
        assertTrue(stored.isRevoked());
        verify(repository, times(2)).save(any());
    }

    @Test
    void refreshAccessTokenThrowsForInvalidToken() {
        when(repository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.refreshAccessToken("invalid"));
    }

    @Test
    void refreshAccessTokenThrowsForRevokedToken() {
        RefreshToken stored = new RefreshToken();
        stored.setRevoked(true);
        stored.setExpiresAt(LocalDateTime.now().plusDays(5));

        when(repository.findByToken(any())).thenReturn(Optional.of(stored));

        assertThrows(IllegalArgumentException.class, () -> service.refreshAccessToken("any-token"));
    }

    @Test
    void refreshAccessTokenThrowsForExpiredToken() {
        RefreshToken stored = new RefreshToken();
        stored.setRevoked(false);
        stored.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(repository.findByToken(any())).thenReturn(Optional.of(stored));

        assertThrows(IllegalArgumentException.class, () -> service.refreshAccessToken("any-token"));
    }

    @Test
    void revokeAllUserTokensDelegatesToRepository() {
        service.revokeAllUserTokens("user-1");
        verify(repository).revokeAllByUserId("user-1");
    }
}
