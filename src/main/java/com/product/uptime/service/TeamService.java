package com.product.uptime.service;

import com.product.uptime.entity.Team;
import com.product.uptime.entity.TeamMember;
import com.product.uptime.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Get a team by user ID
     */
    public Team getTeamByUserId(String userId) {
        return teamRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found for user: " + userId));
    }

    /**
     * Create a new team for a user if one doesn't exist
     */
    public Team createTeam(String userId) {
        Optional<Team> existingTeam = teamRepository.findByUserId(userId);
        if (existingTeam.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team already exists for this user");
        }

        Team newTeam = new Team();
        newTeam.setUserId(userId);
        newTeam.setTeamMembers(new ArrayList<>());
        return teamRepository.save(newTeam);
    }

    /**
     * Add a new member to the team
     */
    public TeamMember addTeamMember(String userId, TeamMember teamMember) {
        Team team = getOrCreateTeam(userId);

        // Check if email already exists in team
        boolean emailExists = team.getTeamMembers().stream()
                .anyMatch(member -> member.getEmail().equals(teamMember.getEmail()));

        if (emailExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team member with this email already exists");
        }

        // Set active by default if not specified
        if (!teamMember.isActive()) {
            teamMember.setActive(true);
        }

        team.getTeamMembers().add(teamMember);
        teamRepository.save(team);

        return teamMember;
    }

    /**
     * Get or create a team for a user
     */
    private Team getOrCreateTeam(String userId) {
        Optional<Team> existingTeam = teamRepository.findByUserId(userId);
        if (existingTeam.isPresent()) {
            return existingTeam.get();
        } else {
            Team newTeam = new Team();
            newTeam.setUserId(userId);
            newTeam.setTeamMembers(new ArrayList<>());
            return teamRepository.save(newTeam);
        }
    }

    /**
     * Get all team members for a user
     */
    public List<TeamMember> getAllTeamMembers(String userId) {
        Team team = getTeamByUserId(userId);
        return team.getTeamMembers();
    }

    /**
     * Update a team member by email
     */
    public TeamMember updateTeamMember(String userId, String email, TeamMember updatedMember) {
        Team team = getTeamByUserId(userId);

        List<TeamMember> members = team.getTeamMembers();
        boolean updated = false;

        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getEmail().equals(email)) {
                // Preserve the email as it's used as the identifier
                updatedMember.setEmail(email);
                members.set(i, updatedMember);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found with email: " + email);
        }

        team.setTeamMembers(members);
        teamRepository.save(team);

        return updatedMember;
    }

    /**
     * Delete a team member by email
     */
    public void deleteTeamMember(String userId, String email) {
        Team team = getTeamByUserId(userId);

        List<TeamMember> updatedMembers = team.getTeamMembers().stream()
                .filter(member -> !member.getEmail().equals(email))
                .collect(Collectors.toList());

        if (updatedMembers.size() == team.getTeamMembers().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found with email: " + email);
        }

        team.setTeamMembers(updatedMembers);
        teamRepository.save(team);
    }

    /**
     * Update a team member's active status
     */
    public TeamMember updateMemberActiveStatus(String userId, String email, boolean active) {
        Team team = getTeamByUserId(userId);

        TeamMember targetMember = null;
        for (TeamMember member : team.getTeamMembers()) {
            if (member.getEmail().equals(email)) {
                member.setActive(active);
                targetMember = member;
                break;
            }
        }

        if (targetMember == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found with email: " + email);
        }

        teamRepository.save(team);
        return targetMember;
    }

    /**
     * Get all active team members' emails
     */
    public List<String> getActiveTeamMemberEmails(String userId) {
        Team team = getTeamByUserId(userId);

        return team.getTeamMembers().stream()
                .filter(TeamMember::isActive)
                .map(TeamMember::getEmail)
                .collect(Collectors.toList());
    }

//    /**
//     * Get all active team members' emails using MongoTemplate
//     */
//    public List<String> getActiveTeamMemberEmailsWithMongoTemplate(String userId) {
//        Query query = new Query(Criteria.where("userId").is(userId));
//        Team team = mongoTemplate.findOne(query, Team.class);
//
//        if (team == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found for user: " + userId);
//        }
//
//        return team.getTeamMembers().stream()
//                .filter(TeamMember::isActive)
//                .map(TeamMember::getEmail)
//                .collect(Collectors.toList());
//    }
}