package com.product.uptime.service;

import com.product.uptime.entity.User;
import com.product.uptime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository= userRepository;
    }
    public User getUser(String email){
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            return user;
        }
        return null;
    }
    public String getCurrentUserID() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            email =  ((UserDetails) principal).getUsername();
        } else {
            email= principal.toString();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not Found "+ email));
        return user.getId();
    }
}
