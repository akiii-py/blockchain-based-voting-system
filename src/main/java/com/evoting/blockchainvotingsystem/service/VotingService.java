package com.evoting.blockchainvotingsystem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evoting.blockchainvotingsystem.model.Candidate;
import com.evoting.blockchainvotingsystem.model.Election;
import com.evoting.blockchainvotingsystem.model.User;
import com.evoting.blockchainvotingsystem.model.Vote;
import com.evoting.blockchainvotingsystem.repository.CandidateRepository;
import com.evoting.blockchainvotingsystem.repository.ElectionRepository;
import com.evoting.blockchainvotingsystem.repository.UserRepository;
import com.evoting.blockchainvotingsystem.repository.VoteRepository;

@Service
public class VotingService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final BlockchainService blockchainService;
    private final ReceiptService receiptService;
    private final BulletinBoardService bulletinBoardService;

    public VotingService(VoteRepository voteRepository, UserRepository userRepository,
                        CandidateRepository candidateRepository, ElectionRepository electionRepository,
                        BlockchainService blockchainService, ReceiptService receiptService,
                        BulletinBoardService bulletinBoardService) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.blockchainService = blockchainService;
        this.receiptService = receiptService;
        this.bulletinBoardService = bulletinBoardService;
    }

    @Transactional
    public Vote castVote(Long userId, Long candidateId, Long electionId) {
        // Check if user has already voted
        if (voteRepository.existsByUserIdAndElectionId(userId, electionId)) {
            throw new RuntimeException("User has already voted in this election");
        }

        // Check if election is active
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty() || !electionOpt.get().isActive()) {
            throw new RuntimeException("Election is not active");
        }

        Election election = electionOpt.get();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(election.getStartTime()) || now.isAfter(election.getEndTime())) {
            throw new RuntimeException("Election is not currently running");
        }

        // Check if candidate exists in the election
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty() || !candidateOpt.get().getElectionId().equals(electionId)) {
            throw new RuntimeException("Invalid candidate for this election");
        }

        // Generate vote hash for cryptographic receipt
        String timestamp = LocalDateTime.now().toString();
        String voteHash = receiptService.generateVoteHash(userId, candidateId, electionId, timestamp);

        // Cast vote on blockchain
        String transactionHash = blockchainService.castVoteOnBlockchain(
            electionId, candidateId, voteHash).join();

        // Get current block number
        Long blockNumber = blockchainService.getBlockNumber().join().longValue();

        // Generate cryptographic receipt
        String receiptSignature = receiptService.generateReceipt(voteHash, transactionHash);

        // Save vote in database with cryptographic data
        Vote vote = new Vote(userId, candidateId, electionId, transactionHash);
        vote.setVoteHash(voteHash);
        vote.setReceiptSignature(receiptSignature);
        vote.setPublicKey(receiptService.getPublicKey());
        vote.setBlockNumber(blockNumber);
        vote.setVerified(true); // Initially verified since we just created it
        vote = voteRepository.save(vote);

        // Publish vote to public bulletin board for transparency
        bulletinBoardService.publishVoteToBulletinBoard(vote);

        // Update user's vote status
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setHasVoted(true);
            userRepository.save(user);
        }

        // Update candidate vote count
        Candidate candidate = candidateOpt.get();
        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);

        return vote;
    }

    public List<Vote> getVotesByElection(Long electionId) {
        return voteRepository.findByElectionId(electionId);
    }

    public boolean hasUserVoted(Long userId, Long electionId) {
        return voteRepository.existsByUserIdAndElectionId(userId, electionId);
    }

    public Optional<Vote> getVoteByUserAndElection(Long userId, Long electionId) {
        return voteRepository.findByUserIdAndElectionId(userId, electionId);
    }
}
