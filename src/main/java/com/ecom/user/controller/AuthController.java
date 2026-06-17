package com.ecom.user.controller;

import com.ecom.user.dto.*;
import com.ecom.user.service.AuthService;
import com.ecom.user.service.EmailVerificationService;
import com.ecom.user.service.PasswordResetService;
import com.ecom.user.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, EmailVerificationService emailVerificationService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.refreshAccessToken(request.getRefreshToken());
    }

    @PostMapping("/verify-email")
    public Map<String, String> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        emailVerificationService.verify(request.getToken());
        return Map.of("message", "Email verified successfully");
    }

    @PostMapping("/resend-verification")
    public Map<String, String> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        String token = emailVerificationService.resendToken(request.getEmail());
        return Map.of("message", "Verification email sent (stub)", "token", token);
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = passwordResetService.createToken(request.getEmail()).getToken();
        return Map.of("message", "Password reset email sent (stub)", "token", token);
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return Map.of("message", "Password reset successfully");
    }
}
