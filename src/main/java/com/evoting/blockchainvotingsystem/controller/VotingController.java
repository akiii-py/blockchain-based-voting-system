package com.evoting.blockchainvotingsystem.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evoting.blockchainvotingsystem.model.Vote;
import com.evoting.blockchainvotingsystem.repository.UserRepository;
import com.evoting.blockchainvotingsystem.service.VotingService;

@RestController
@RequestMapping("/voting")
public class VotingController {

    private final VotingService votingService;
    private final UserRepository userRepository;

    public VotingController(VotingService votingService, UserRepository userRepository) {
        this.votingService = votingService;
        this.userRepository = userRepository;
    }

    @PostMapping("/vote")
    public ResponseEntity<Vote> castVote(@RequestParam Long userId,
                                        @RequestParam Long candidateId,
                                        @RequestParam Long electionId) {
        Vote vote = votingService.castVote(userId, candidateId, electionId);
        return ResponseEntity.ok(vote);
    }

    @GetMapping("/election/{electionId}/votes")
    public ResponseEntity<List<Vote>> getVotesByElection(@PathVariable Long electionId) {
        List<Vote> votes = votingService.getVotesByElection(electionId);
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/user/{userId}/election/{electionId}/status")
    public ResponseEntity<Boolean> hasUserVoted(@PathVariable Long userId, @PathVariable Long electionId) {
        boolean hasVoted = votingService.hasUserVoted(userId, electionId);
        return ResponseEntity.ok(hasVoted);
    }

    @GetMapping("/user/{userId}/election/{electionId}")
    public ResponseEntity<Vote> getVoteByUserAndElection(@PathVariable Long userId, @PathVariable Long electionId) {
        Optional<Vote> vote = votingService.getVoteByUserAndElection(userId, electionId);
        return vote.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
