package com.anuj.quantityMeasurement.security;

import com.anuj.quantityMeasurement.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;


	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
			) throws ServletException, IOException {

		// Get the Authorization header
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String username;

		// Check if Authorization header is present and starts with "Bearer "
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		// Extract token (remove "Bearer " which is 7 characters)
		jwt = authHeader.substring(7);
		try {
			// Extract username from token
			username = jwtService.extractUsername(jwt);

			/*
			 * Check two conditions:
			 *   -> username is not null (token was parseable)
			 *   -> user is not already authenticated for this request
			 *      (SecurityContextHolder.getContext().getAuthentication() == null
			 *       means: not yet authenticated)
			 *
			 * We only proceed if both are true.
			 */
			if (username != null
					&& SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails =
						this.userDetailsService.loadUserByUsername(username);

				// Validate the token against this user
				if (jwtService.isTokenValid(jwt, userDetails)) {

					//Create an Authentication object.

					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(
									userDetails,
									null,
									userDetails.getAuthorities()
									);

					authToken.setDetails(
							new WebAuthenticationDetailsSource().buildDetails(request)
							);

					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}catch (Exception e) {
			// Token is invalid, expired, or user was deleted from in-memory DB.
			// Do NOT return 401 here, just let the request proceed unauthenticated.
			// Spring Security will enforce authorization rules based on URL later.
			SecurityContextHolder.clearContext();
		}


		// Continue the filter chain — pass to next filter/controller
		filterChain.doFilter(request, response);
	}
}