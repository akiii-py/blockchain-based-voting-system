package com.evoting.blockchainvotingsystem.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evoting.blockchainvotingsystem.model.Election;
import com.evoting.blockchainvotingsystem.model.User;
import com.evoting.blockchainvotingsystem.service.ElectionService;
import com.evoting.blockchainvotingsystem.service.UserService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ElectionService electionService;

    public AdminController(UserService userService, ElectionService electionService) {
        this.userService = userService;
        this.electionService = electionService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestParam String username,
                                          @RequestParam String password,
                                          @RequestParam String email,
                                          @RequestParam String fullName,
                                          @RequestParam String voterId,
                                          @RequestParam String role) {
        User user = userService.registerUser(username, password, email, fullName, voterId, role);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        // In a real implementation, you'd have a method to get all users
        // For now, return empty list
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/elections")
    public ResponseEntity<Election> createElection(@RequestParam String title,
                                                  @RequestParam String description,
                                                  @RequestParam String startTime,
                                                  @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        Election election = electionService.createElection(title, description, start, end);
        return ResponseEntity.ok(election);
    }

    @PutMapping("/elections/{id}/activate")
    public ResponseEntity<Void> activateElection(@PathVariable Long id) {
        electionService.activateElection(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/elections/{id}/deactivate")
    public ResponseEntity<Void> deactivateElection(@PathVariable Long id) {
        electionService.deactivateElection(id);
        return ResponseEntity.ok().build();
    }
}
