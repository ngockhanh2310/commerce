package com.khanh.commerce.service;

import com.khanh.commerce.dto.auth.AuthRequest;
import com.khanh.commerce.dto.auth.AuthResponse;
import com.khanh.commerce.dto.auth.RegisterRequest;
import com.khanh.commerce.entity.User;
import com.khanh.commerce.exception.DuplicateException;
import com.khanh.commerce.model.Role;
import com.khanh.commerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        List<String> existingUsers = new ArrayList<>();

        if (userRepository.existsByUsername(request.username())) {
            existingUsers.add("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            existingUsers.add("Email already taken");
        }

        if (!existingUsers.isEmpty()) {
            throw new DuplicateException(existingUsers);
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return new AuthResponse(
                request.username(),
                Role.USER,
                user.getEmail(),
                user.getFullName(),
                user.getAddress(),
                user.getPhoneNumber(),
                null,
                null
        );
    }

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(
                request.username(),
                user.getRole(),
                user.getEmail(),
                user.getFullName(),
                user.getAddress(),
                user.getPhoneNumber(),
                jwtToken,
                refreshToken
        );
    }

    @Transactional
    public AuthResponse refreshToken(HttpServletRequest request) {
        // 1. Lấy Refresh Token từ Header "Authorization"
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        refreshToken = authHeader.substring(7);

        username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);

                return new AuthResponse(
                        user.getUsername(),
                        user.getRole(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getAddress(),
                        user.getPhoneNumber(),
                        accessToken,
                        refreshToken
                );
            }
        }
        throw new DuplicateException(List.of("Refresh token is not valid"));
    }
}
