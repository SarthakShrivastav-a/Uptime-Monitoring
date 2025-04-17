package com.product.uptime.controller;

import com.product.uptime.dto.CompleteProfileRequest;
import com.product.uptime.entity.User;
import com.product.uptime.repository.UserRepository;
import com.product.uptime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository){
        this.userService=userService;
        this.userRepository=userRepository;
    }

    @GetMapping("/details")
    public ResponseEntity<User> getUSer(){
        System.out.println("Hello");
        String id = userService.getCurrentUserID();
        Optional<User> userOpt=userRepository.findById(id);
        if(userOpt.isPresent()){
            String email = userOpt.get().getEmail();
            System.out.println(email);
            User user = userService.getUser(email);
            System.out.println(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody CompleteProfileRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user information
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCompany(request.getCompanyName());
        System.out.println(user);
        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }


}
