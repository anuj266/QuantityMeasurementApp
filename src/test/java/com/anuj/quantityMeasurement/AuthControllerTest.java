package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.model.AuthResponse;
import com.anuj.quantityMeasurement.model.LoginRequest;
import com.anuj.quantityMeasurement.model.RegisterRequest;
import com.anuj.quantityMeasurement.Controller.AuthController;
import com.anuj.quantityMeasurement.service.AuthService;
import com.anuj.quantityMeasurement.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

 
    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void testRegister_ValidInput_Returns201() throws Exception {
        AuthResponse mockResponse = AuthResponse.builder()
            .token("fake-jwt-token")
            .username("vikash")
            .role("USER")
            .message("Registration successful")
            .build();

        Mockito.when(authService.register(Mockito.any()))
               .thenReturn(mockResponse);

        RegisterRequest request =
            new RegisterRequest("vikash", "password123", "vikash@test.com");

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.token").value("fake-jwt-token"))
               .andExpect(jsonPath("$.username").value("vikash"))
               .andExpect(jsonPath("$.role").value("USER"))
               .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @WithMockUser
    void testRegister_EmptyUsername_Returns400() throws Exception {
        RegisterRequest badRequest =
            new RegisterRequest("", "password123", "vikash@test.com");

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(badRequest)))
               .andExpect(status().isBadRequest());

        Mockito.verify(authService, Mockito.never()).register(Mockito.any());
    }

    @Test
    @WithMockUser
    void testRegister_InvalidEmail_Returns400() throws Exception {
        RegisterRequest badRequest =
            new RegisterRequest("vikash", "password123", "not-an-email");

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(badRequest)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testRegister_ShortPassword_Returns400() throws Exception {
        RegisterRequest badRequest =
            new RegisterRequest("vikash", "abc", "vikash@test.com");

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(badRequest)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testLogin_ValidCredentials_Returns200() throws Exception {
        AuthResponse mockResponse = AuthResponse.builder()
            .token("fake-jwt-token")
            .username("vikash")
            .role("USER")
            .message("Login successful")
            .build();

        Mockito.when(authService.login(Mockito.any()))
               .thenReturn(mockResponse);

        LoginRequest request = new LoginRequest("vikash", "password123");

        mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value("fake-jwt-token"))
               .andExpect(jsonPath("$.username").value("vikash"))
               .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @WithMockUser
    void testLogin_MissingPassword_Returns400() throws Exception {
        LoginRequest badRequest = new LoginRequest("vikash", "");

        mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(badRequest)))
               .andExpect(status().isBadRequest());

        Mockito.verify(authService, Mockito.never()).login(Mockito.any());
    }
}