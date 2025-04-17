package com.product.uptime.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private AuthUserService authUserService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        logger.debug("AuthTokenFilter triggered for URI: {}", requestUri);

        // Skip JWT processing for OAuth2 authorization endpoints
        if (requestUri.contains("/oauth2/") || requestUri.contains("/login/oauth2/")) {
            logger.debug("Skipping JWT authentication for OAuth2 endpoint: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtility.validateJwtToken(jwt)) {
                String username = jwtUtility.getUserNameFromJwtToken(jwt);
                logger.debug("Valid JWT found for user: {}", username);

                try {
                    UserDetails userDetails = authUserService.loadUserByUsername(username);

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("User {} successfully authenticated with JWT.", username);
                    }
                } catch (Exception e) {
                    logger.error("Error loading user details for JWT user {}: {}", username, e.getMessage());
                    // Continue filter chain without authentication - security config will determine access
                }
            } else {
                logger.debug("No valid JWT found for request: {}", requestUri);
            }
        } catch (Exception e) {
            logger.error("Authentication processing error: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtility.getJwtFromHeader(request);
        logger.debug("Extracted JWT from request: {}", jwt);
        return jwt;
    }
}
