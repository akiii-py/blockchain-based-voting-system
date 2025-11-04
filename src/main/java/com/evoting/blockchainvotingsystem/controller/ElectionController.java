package com.evoting.blockchainvotingsystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evoting.blockchainvotingsystem.model.Candidate;
import com.evoting.blockchainvotingsystem.model.Election;
import com.evoting.blockchainvotingsystem.service.ElectionService;

@RestController
@RequestMapping("/elections")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<Election>> getActiveElections() {
        List<Election> elections = electionService.getActiveElections();
        return ResponseEntity.ok(elections);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Election>> getAllElections() {
        List<Election> elections = electionService.getAllElections();
        return ResponseEntity.ok(elections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable Long id) {
        return electionService.getElectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/candidates")
    public ResponseEntity<List<Candidate>> getCandidatesByElectionId(@PathVariable Long id) {
        List<Candidate> candidates = electionService.getCandidatesByElectionId(id);
        return ResponseEntity.ok(candidates);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Election> createElection(@RequestParam String title,
                                                  @RequestParam String description) {
        Election election = electionService.createElection(title, description);
        return ResponseEntity.ok(election);
    }

    @PostMapping("/{electionId}/candidates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Candidate> addCandidate(@PathVariable Long electionId,
                                                 @RequestParam String name,
                                                 @RequestParam String party,
                                                 @RequestParam String description) {
        Candidate candidate = electionService.addCandidate(electionId, name, party, description);
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateElection(@PathVariable Long id) {
        electionService.activateElection(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateElection(@PathVariable Long id) {
        electionService.deactivateElection(id);
        return ResponseEntity.ok().build();
    }
}
