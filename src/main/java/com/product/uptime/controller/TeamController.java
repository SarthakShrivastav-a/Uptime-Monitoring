package com.product.uptime.controller;

import com.product.uptime.entity.Team;
import com.product.uptime.entity.TeamMember;
import com.product.uptime.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/{userId}")
    public ResponseEntity<Team> createTeam(@PathVariable String userId) {
        Team team = teamService.createTeam(userId);
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Team> getTeam(@PathVariable String userId) {
        Team team = teamService.getTeamByUserId(userId);
        return ResponseEntity.ok(team);
    }

    @GetMapping("/{userId}/members")
    public ResponseEntity<List<TeamMember>> getAllTeamMembers(@PathVariable String userId) {
        List<TeamMember> members = teamService.getAllTeamMembers(userId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{userId}/members")
    public ResponseEntity<TeamMember> addTeamMember(
            @PathVariable String userId,
            @RequestBody TeamMember teamMember) {
        TeamMember addedMember = teamService.addTeamMember(userId, teamMember);
        return new ResponseEntity<>(addedMember, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/members/{email}")
    public ResponseEntity<TeamMember> updateTeamMember(
            @PathVariable String userId,
            @PathVariable String email,
            @RequestBody TeamMember teamMember) {
        TeamMember updatedMember = teamService.updateTeamMember(userId, email, teamMember);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{userId}/members/{email}")
    public ResponseEntity<Void> deleteTeamMember(
            @PathVariable String userId,
            @PathVariable String email) {
        teamService.deleteTeamMember(userId, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/members/{email}/active")
    public ResponseEntity<TeamMember> updateMemberActiveStatus(
            @PathVariable String userId,
            @PathVariable String email,
            @RequestBody Map<String, Boolean> status) {
        Boolean active = status.get("active");
        if (active == null) {
            return ResponseEntity.badRequest().build();
        }

        TeamMember member = teamService.updateMemberActiveStatus(userId, email, active);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/{userId}/members/active/emails")
    public ResponseEntity<List<String>> getActiveTeamMemberEmails(@PathVariable String userId) {
        List<String> emails = teamService.getActiveTeamMemberEmails(userId);
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/{userId}/members/active/emails/mongo-template")
    public ResponseEntity<List<String>> getActiveTeamMemberEmailsWithMongoTemplate(@PathVariable String userId) {
        List<String> emails = teamService.getActiveTeamMemberEmailsWithMongoTemplate(userId);
        return ResponseEntity.ok(emails);
    }
}