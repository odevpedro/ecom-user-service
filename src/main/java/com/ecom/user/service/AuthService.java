package com.ecom.user.service;

import com.ecom.user.dto.CreateUserRequest;
import com.ecom.user.dto.LoginRequest;
import com.ecom.user.dto.LoginResponse;
import com.ecom.user.dto.UserResponse;
import com.ecom.user.model.RefreshToken;
import com.ecom.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(CreateUserRequest request) {
        return userService.create(request);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new LoginResponse(token, jwtService.getExpiration(), refreshToken.getToken(), UserResponse.fromEntity(user));
    }
}
