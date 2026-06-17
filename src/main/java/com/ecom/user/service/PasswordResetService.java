package com.ecom.user.service;

import com.ecom.user.model.PasswordReset;
import com.ecom.user.model.User;
import com.ecom.user.repository.PasswordResetRepository;
import com.ecom.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetRepository repository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordReset createToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PasswordReset pr = new PasswordReset();
        pr.setUserId(user.getId());
        pr.setToken(UUID.randomUUID().toString());
        pr.setExpiresAt(LocalDateTime.now().plusHours(1));
        return repository.save(pr);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordReset pr = repository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (pr.getUsedAt() != null) {
            throw new IllegalArgumentException("Reset token already used");
        }

        if (pr.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token expired");
        }

        User user = userRepository.findById(pr.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        pr.setUsedAt(LocalDateTime.now());
        repository.save(pr);
    }
}
