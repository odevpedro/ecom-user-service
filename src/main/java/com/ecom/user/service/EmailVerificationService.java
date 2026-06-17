package com.ecom.user.service;

import com.ecom.user.model.EmailVerification;
import com.ecom.user.model.User;
import com.ecom.user.repository.EmailVerificationRepository;
import com.ecom.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository repository;
    private final UserRepository userRepository;

    public EmailVerificationService(EmailVerificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public EmailVerification createToken(String userId) {
        EmailVerification ev = new EmailVerification();
        ev.setUserId(userId);
        ev.setToken(UUID.randomUUID().toString());
        ev.setExpiresAt(LocalDateTime.now().plusDays(1));
        return repository.save(ev);
    }

    @Transactional
    public void verify(String token) {
        EmailVerification ev = repository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (ev.getVerifiedAt() != null) {
            throw new IllegalArgumentException("Email already verified");
        }

        if (ev.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token expired");
        }

        ev.setVerifiedAt(LocalDateTime.now());
        repository.save(ev);
    }

    public String resendToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        EmailVerification ev = createToken(user.getId());
        return ev.getToken();
    }
}
