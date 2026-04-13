package com.anuj.quantityMeasurement.config;

import com.anuj.quantityMeasurement.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider  authenticationProvider;

    private static final String[] PUBLIC_URLS = {
        "/auth/**",
        "/h2-console/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api-docs/**",
        "/actuator/**",
        "/api/v1/quantities/compare",
        "/api/v1/quantities/add",
        "/api/v1/quantities/subtract",
        "/api/v1/quantities/divide",
        "/api/v1/quantities/convert"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write(
                            "{\"status\":401," +
                            "\"error\":\"Unauthorized\"," +
                            "\"message\":\"" + authException.getMessage() + "\"}"
                        );
                    })
                )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login")
                .defaultSuccessUrl("/api/v1/quantities/history/operation/compare", true)
                .failureUrl("/auth/login?error=true")
            );

        return http.build();
    }
}