package com.anuj.quantityMeasurement.service;

import com.anuj.quantityMeasurement.model.*;
import com.anuj.quantityMeasurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
 * AuthService handles the business logic for:
 *   -> User registration
 *   -> User login
 *
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final AuthenticationManager authenticationManager;

    // register new user
    
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user: {}", request.getUsername());

        // Check username not already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                "Username '" + request.getUsername() + "' is already taken");
        }

        // Check email not already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .role(Role.USER)
            .build();

        // Save user to database
        userRepository.save(user);
        logger.info("User '{}' registered successfully", request.getUsername());

        // Generate JWT tokens
        String jwtToken     = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Return response with tokens
        return AuthResponse.builder()
            .token(jwtToken)
            .refreshToken(refreshToken)
            .username(user.getUsername())
            .role(user.getRole().name())
            .message("Registration successful")
            .build();
    }

    // login existing user
    
    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());

        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + request.getUsername()));

        logger.info("User '{}' logged in successfully", request.getUsername());

        // Generate new tokens
        String jwtToken     = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
            .token(jwtToken)
            .refreshToken(refreshToken)
            .username(user.getUsername())
            .role(user.getRole().name())
            .message("Login successful")
            .build();
    }
    
    public AuthResponse registerAdmin(RegisterRequest request) {
        logger.info("Registering new ADMIN user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .role(Role.ADMIN)   // ← always ADMIN for this endpoint
            .build();

        userRepository.save(user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        String jwtToken     = jwtService.generateToken(extraClaims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
            .token(jwtToken)
            .refreshToken(refreshToken)
            .username(user.getUsername())
            .role(user.getRole().name())
            .message("Admin registration successful")
            .build();
    }
}