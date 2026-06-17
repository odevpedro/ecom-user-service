package com.ecom.user.service;

import com.ecom.user.dto.RefreshTokenResponse;
import com.ecom.user.model.RefreshToken;
import com.ecom.user.repository.RefreshTokenRepository;
import com.ecom.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final RefreshTokenRepository repository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository repository, JwtService jwtService, UserRepository userRepository) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String userId) {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS));
        rt.setRevoked(false);
        return repository.save(rt);
    }

    @Transactional
    public RefreshTokenResponse refreshAccessToken(String refreshTokenString) {
        RefreshToken stored = repository.findByToken(refreshTokenString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new IllegalArgumentException("Refresh token revoked");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        stored.setRevoked(true);
        repository.save(stored);

        String email = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getEmail();

        String newJwt = jwtService.generateToken(stored.getUserId(), email);
        RefreshToken newRt = createRefreshToken(stored.getUserId());

        return new RefreshTokenResponse(newJwt, newRt.getToken(), jwtService.getExpiration());
    }

    @Transactional
    public void revokeAllUserTokens(String userId) {
        repository.revokeAllByUserId(userId);
    }
}
