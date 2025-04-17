package com.product.uptime.oauth2;

import com.product.uptime.entity.AuthUser;
import com.product.uptime.entity.User;
import com.product.uptime.jwt.JwtUtility;
import com.product.uptime.repository.AuthUserRepository;
import com.product.uptime.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        Optional<AuthUser> existingUser = authUserRepository.findByEmail(email);
        AuthUser authUser;

        if (existingUser.isPresent()) {
            // User exists, update OAuth2 info if needed
            authUser = existingUser.get();
            authUser.setProviderId(providerId);
            authUser.setProvider(provider);
            authUserRepository.save(authUser);
        } else {
            // Create new user with OAuth2 info
            authUser = new AuthUser();
            authUser.setEmail(email);
            authUser.setProviderId(providerId);
            authUser.setProvider(provider);
            authUser.setRole("USER");
            authUser = authUserRepository.save(authUser);

            // Create user profile - this is where we'll redirect to complete profile
            User user = new User();
            user.setAuthUserId(authUser.getId());
            user.setEmail(email);
            user.setFirstName(oAuth2User.getAttribute("given_name"));
            user.setLastName(oAuth2User.getAttribute("family_name"));
            userRepository.save(user);
        }

        // Create UserDetails object manually for JWT generation
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(authUser.getRole())
        );

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                email, "", authorities
        );

        // Generate JWT token using existing method
        String token = jwtUtility.generateTokenFromUsername(userDetails);

        // Redirect to frontend with token
        String redirectUrl = "http://localhost:3000/oauth2/callback?token=" + token;

        // For new users, add a parameter to indicate profile completion is needed
        if (!existingUser.isPresent()) {
            redirectUrl += "&newUser=true";
        }

        response.sendRedirect(redirectUrl);
    }
}