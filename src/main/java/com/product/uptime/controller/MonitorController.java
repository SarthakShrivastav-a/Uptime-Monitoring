package com.product.uptime.controller;

import com.product.uptime.dto.MonitorDetailsDTO;
import com.product.uptime.dto.MonitorDto;
import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.dto.MonitorUpdateDTO;
import com.product.uptime.entity.ErrorCondition;
import com.product.uptime.entity.Monitor;
import com.product.uptime.entity.User;
import com.product.uptime.exception.EntityNotFoundException;
import com.product.uptime.exception.MonitorNotFoundException;
import com.product.uptime.repository.MonitorRepository;
import com.product.uptime.repository.UserRepository;
import com.product.uptime.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    private UserRepository userRepository;
    private MonitorService monitorService;
    private MonitorRepository monitorRepository;
    @Autowired
    public MonitorController(MonitorService monitorService,UserRepository userRepository,MonitorRepository monitorRepository){
        this.monitorService=monitorService;
        this.userRepository=userRepository;
        this.monitorRepository=monitorRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Monitor> addMonitor(@RequestBody MonitorDto monitorDto) {
        String id = getCurrentUserID();
        Monitor monitor =  monitorService.createMonitor(id,monitorDto.getUrl(),monitorDto.getErrorCondition());
        return new ResponseEntity<>(monitor, HttpStatus.CREATED);
    }
    @GetMapping("/fetch")
    public ResponseEntity<List<Monitor>> getMonitors() {
        String id = getCurrentUserID();
        List<Monitor> monitors = monitorRepository.findAllByUserId(id);
        return ResponseEntity.ok(monitors); //if null, in the ui show no monitors created And highlight the option to create new
    }
    @GetMapping("/fetch/{id}")
    public ResponseEntity<Monitor> getById(@PathVariable String id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new MonitorNotFoundException("Monitor not found with ID: " + id));

        String userId = getCurrentUserID();
        String usId = monitor.getUserId();

        if (usId.equals(userId)) {
            return new ResponseEntity<>(monitor, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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

    @GetMapping("/{monitorId}/details")
    public ResponseEntity<MonitorDetailsDTO> getMonitorDetails(@PathVariable String monitorId) {
        MonitorDetailsDTO details = monitorService.getMonitorDetails(monitorId);
        return ResponseEntity.ok(details);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitor(@PathVariable String id) throws EntityNotFoundException {
        monitorService.deleteMonitor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PatchMapping("/{id}/error-condition")
    public ResponseEntity<?> updateMonitorErrorCondition(
            @PathVariable String id,
            @RequestBody ErrorCondition errorCondition) {

        try {
            // Validate the error condition
            if (errorCondition.getTriggerOn() == null || errorCondition.getTriggerOn().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("TriggerOn is required in error condition");
            }

            // Update the monitor's error condition
            Monitor updatedMonitor = monitorService.updateMonitorErrorCondition(id, errorCondition);

            // Return the updated monitor
            return ResponseEntity.ok(updatedMonitor);

        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update monitor error condition");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

}
