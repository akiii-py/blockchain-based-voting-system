package com.evoting.blockchainvotingsystem.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.evoting.blockchainvotingsystem.model.Vote;
import com.evoting.blockchainvotingsystem.repository.VoteRepository;

@Service
public class BulletinBoardService {

    private final VoteRepository voteRepository;
    private final ReceiptService receiptService;

    // In-memory bulletin board for public verification
    private final Map<String, BulletinBoardEntry> publicBulletinBoard = new ConcurrentHashMap<>();

    public BulletinBoardService(VoteRepository voteRepository, ReceiptService receiptService) {
        this.voteRepository = voteRepository;
        this.receiptService = receiptService;
    }

    public void publishVoteToBulletinBoard(Vote vote) {
        BulletinBoardEntry entry = new BulletinBoardEntry(
            vote.getVoteHash(),
            vote.getTransactionHash(),
            vote.getReceiptSignature(),
            vote.getElectionId(),
            vote.getCandidateId(),
            vote.getBlockNumber(),
            vote.getVotedAt()
        );
        publicBulletinBoard.put(vote.getVoteHash(), entry);
    }

    public boolean verifyVoteOnBulletinBoard(String voteHash) {
        BulletinBoardEntry entry = publicBulletinBoard.get(voteHash);
        if (entry == null) {
            return false;
        }

        // Verify receipt signature
        return receiptService.verifyReceipt(
            entry.getVoteHash(),
            entry.getTransactionHash(),
            entry.getReceiptSignature()
        );
    }

    public List<BulletinBoardEntry> getElectionBulletinBoard(Long electionId) {
        return publicBulletinBoard.values().stream()
            .filter(entry -> entry.getElectionId().equals(electionId))
            .collect(Collectors.toList());
    }

    public Map<Long, Long> getElectionResults(Long electionId) {
        return publicBulletinBoard.values().stream()
            .filter(entry -> entry.getElectionId().equals(electionId))
            .collect(Collectors.groupingBy(
                BulletinBoardEntry::getCandidateId,
                Collectors.counting()
            ));
    }

    public boolean verifyElectionTally(Long electionId, Map<Long, Long> claimedResults) {
        Map<Long, Long> actualResults = getElectionResults(electionId);

        // Compare claimed results with bulletin board results
        if (claimedResults.size() != actualResults.size()) {
            return false;
        }

        for (Map.Entry<Long, Long> entry : claimedResults.entrySet()) {
            Long candidateId = entry.getKey();
            Long claimedCount = entry.getValue();
            Long actualCount = actualResults.get(candidateId);

            if (!claimedCount.equals(actualCount)) {
                return false;
            }
        }

        return true;
    }

    public static class BulletinBoardEntry {
        private final String voteHash;
        private final String transactionHash;
        private final String receiptSignature;
        private final Long electionId;
        private final Long candidateId;
        private final Long blockNumber;
        private final java.time.LocalDateTime timestamp;

        public BulletinBoardEntry(String voteHash, String transactionHash, String receiptSignature,
                                Long electionId, Long candidateId, Long blockNumber,
                                java.time.LocalDateTime timestamp) {
            this.voteHash = voteHash;
            this.transactionHash = transactionHash;
            this.receiptSignature = receiptSignature;
            this.electionId = electionId;
            this.candidateId = candidateId;
            this.blockNumber = blockNumber;
            this.timestamp = timestamp;
        }

        // Getters
        public String getVoteHash() { return voteHash; }
        public String getTransactionHash() { return transactionHash; }
        public String getReceiptSignature() { return receiptSignature; }
        public Long getElectionId() { return electionId; }
        public Long getCandidateId() { return candidateId; }
        public Long getBlockNumber() { return blockNumber; }
        public java.time.LocalDateTime getTimestamp() { return timestamp; }
    }
}
