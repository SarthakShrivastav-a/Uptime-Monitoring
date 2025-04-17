package com.product.uptime.controller;

import com.product.uptime.entity.Team;
import com.product.uptime.entity.TeamMember;
import com.product.uptime.entity.User;
import com.product.uptime.repository.UserRepository;
import com.product.uptime.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Extract user ID from the security context
     */
    private String getCurrentUserId() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found " + email));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<Team> createTeam() {
        System.out.println("Create team called");
        String userId = getCurrentUserId();
        Team team = teamService.createTeam(userId);
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Team> getTeam() {
        System.out.println("get team called");
        String userId = getCurrentUserId();
        Team team = teamService.getTeamByUserId(userId);
        System.out.println(team);
        return ResponseEntity.ok(team);
    }

    @GetMapping("/members")
    public ResponseEntity<List<TeamMember>> getAllTeamMembers() {
        String userId = getCurrentUserId();
        List<TeamMember> members = teamService.getAllTeamMembers(userId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/members")
    public ResponseEntity<TeamMember> addTeamMember(@RequestBody TeamMember teamMember) {
        String userId = getCurrentUserId();
        TeamMember addedMember = teamService.addTeamMember(userId, teamMember);
        return new ResponseEntity<>(addedMember, HttpStatus.CREATED);
    }

    @PutMapping("/members/{email}")
    public ResponseEntity<TeamMember> updateTeamMember(
            @PathVariable String email,
            @RequestBody TeamMember teamMember) {
        System.out.println("Update");
        String userId = getCurrentUserId();
        TeamMember updatedMember = teamService.updateTeamMember(userId, email, teamMember);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/members/{email}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable String email) {
        String userId = getCurrentUserId();
        teamService.deleteTeamMember(userId, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{email}/active")
    public ResponseEntity<TeamMember> updateMemberActiveStatus(
            @PathVariable String email,
            @RequestBody Map<String, Boolean> status) {
        System.out.println("Active status");
        String userId = getCurrentUserId();
        Boolean active = status.get("active");
        if (active == null) {
            return ResponseEntity.badRequest().build();
        }

        TeamMember member = teamService.updateMemberActiveStatus(userId, email, active);
        return ResponseEntity.ok(member);
    }

    // Uncomment if you need these endpoints
    /*
    @GetMapping("/members/active/emails")
    public ResponseEntity<List<String>> getActiveTeamMemberEmails() {
        String userId = getCurrentUserId();
        List<String> emails = teamService.getActiveTeamMemberEmails(userId);
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/members/active/emails/mongo-template")
    public ResponseEntity<List<String>> getActiveTeamMemberEmailsWithMongoTemplate() {
        String userId = getCurrentUserId();
        List<String> emails = teamService.getActiveTeamMemberEmailsWithMongoTemplate(userId);
        return ResponseEntity.ok(emails);
    }
    */
}