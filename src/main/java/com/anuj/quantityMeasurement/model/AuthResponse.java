package com.anuj.quantityMeasurement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO returned after successful register or login.
 * Client receives this JSON:
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;           // JWT access token
    private String refreshToken;    // JWT refresh token (for getting new access token)
    private String username;        // so client knows who logged in
    private String role;            // USER or ADMIN
    private String message;         // "Registration successful" / "Login successful"
}
