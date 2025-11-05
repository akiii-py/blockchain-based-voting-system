package com.evoting.blockchainvotingsystem.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evoting.blockchainvotingsystem.model.User;
import com.evoting.blockchainvotingsystem.model.Vote;
import com.evoting.blockchainvotingsystem.repository.UserRepository;
import com.evoting.blockchainvotingsystem.service.BulletinBoardService;
import com.evoting.blockchainvotingsystem.service.ReceiptService;
import com.evoting.blockchainvotingsystem.service.VotingService;

@RestController
@RequestMapping("/voting")
public class VotingController {

    private final VotingService votingService;
    private final UserRepository userRepository;
    private final BulletinBoardService bulletinBoardService;
    private final ReceiptService receiptService;

    public VotingController(VotingService votingService, UserRepository userRepository,
                           BulletinBoardService bulletinBoardService, ReceiptService receiptService) {
        this.votingService = votingService;
        this.userRepository = userRepository;
        this.bulletinBoardService = bulletinBoardService;
        this.receiptService = receiptService;
    }

    @PostMapping("/vote")
    public ResponseEntity<Vote> castVote(@RequestParam Long candidateId,
                                        @RequestParam Long electionId,
                                        @RequestParam(required = false) Long userId) {
        Long resolvedUserId = userId;
        
        // Try to get user from SecurityContext if userId not provided
        if (resolvedUserId == null) {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                String username = auth.getName();
                resolvedUserId = userRepository.findByUsername(username)
                        .map(User::getId)
                        .orElse(null);
            }
        }
        
        if (resolvedUserId == null) {
            return ResponseEntity.status(401).body(null);
        }

        Vote vote = votingService.castVote(resolvedUserId, candidateId, electionId);
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

    @GetMapping("/verify/{voteHash}")
    public ResponseEntity<Map<String, Object>> verifyVoteReceipt(@PathVariable String voteHash) {
        boolean isValid = bulletinBoardService.verifyVoteOnBulletinBoard(voteHash);
        Map<String, Object> response = Map.of(
            "voteHash", voteHash,
            "isValid", isValid,
            "verified", isValid
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bulletin-board/election/{electionId}")
    public ResponseEntity<List<BulletinBoardService.BulletinBoardEntry>> getElectionBulletinBoard(@PathVariable Long electionId) {
        List<BulletinBoardService.BulletinBoardEntry> bulletinBoard = bulletinBoardService.getElectionBulletinBoard(electionId);
        return ResponseEntity.ok(bulletinBoard);
    }

    @GetMapping("/results/election/{electionId}/verify")
    public ResponseEntity<Map<String, Object>> verifyElectionResults(@PathVariable Long electionId,
                                                                   @RequestParam Map<Long, Long> claimedResults) {
        boolean isValid = bulletinBoardService.verifyElectionTally(electionId, claimedResults);
        Map<Long, Long> actualResults = bulletinBoardService.getElectionResults(electionId);

        Map<String, Object> response = Map.of(
            "electionId", electionId,
            "isValid", isValid,
            "claimedResults", claimedResults,
            "actualResults", actualResults
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        String publicKey = receiptService.getPublicKey();
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }
}
