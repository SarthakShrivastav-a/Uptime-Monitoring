package com.product.uptime.controller;

import com.product.uptime.dto.MonitorDto;
import com.product.uptime.entity.Monitor;
import com.product.uptime.entity.User;
import com.product.uptime.repository.UserRepository;
import com.product.uptime.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/monitor")
public class MonitorController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MonitorService monitorService;

    @PostMapping("create")
    public ResponseEntity<?> addMonitor(@RequestBody MonitorDto monitorDto) {
        String id = getCurrentUserID();

        Monitor monitor =  monitorService.createMonitor(id,monitorDto.getUrl(),monitorDto.getErrorCondition());
        return new ResponseEntity<>(monitor, HttpStatus.CREATED);
    }

    private String getCurrentUserID() {
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
