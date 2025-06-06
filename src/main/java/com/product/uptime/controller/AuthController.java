package com.product.uptime.controller;


import com.product.uptime.dto.CompleteProfileRequest;
import com.product.uptime.entity.AuthUser;
import com.product.uptime.dto.LoginRequest;
import com.product.uptime.dto.SignUp;
import com.product.uptime.entity.User;
import com.product.uptime.jwt.JwtUtility;
import com.product.uptime.repository.AuthUserRepository;
import com.product.uptime.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtility jwtUtility;
//
//    @Autowired
//    private AuthUserService authUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtility.generateTokenFromUsername(
                    ((UserDetails) authentication.getPrincipal()));

            logger.info("User {} successfully authenticated, JWT generated.", loginRequest.getEmail());
            return ResponseEntity.ok(jwt);

        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUp signUp) {
        if (authUserRepository.findByEmail(signUp.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists with this email.");
        }

        String encodedPassword = passwordEncoder.encode(signUp.getPassword());


        AuthUser authUser = new AuthUser();
        authUser.setEmail(signUp.getEmail());
        authUser.setPassword(encodedPassword);
        authUser.setRole("USER");
        authUser = authUserRepository.save(authUser);
        User user = new User();
        user.setAuthUserId(authUser.getId());
        user.setFirstName(signUp.getFirstName());
        user.setLastName(signUp.getLastName());
        user.setEmail(signUp.getEmail());
        user.setCompany(signUp.getCompanyName());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

}
