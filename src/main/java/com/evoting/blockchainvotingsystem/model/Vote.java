package com.evoting.blockchainvotingsystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "election_id", nullable = false)
    private Long electionId;

    @Column(name = "transaction_hash", nullable = false)
    private String transactionHash;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "vote_hash")
    private String voteHash;

    @Column(name = "receipt_signature")
    private String receiptSignature;

    @Column(name = "public_key")
    private String publicKey;

    public Vote() {
    }

    public Vote(Long userId, Long candidateId, Long electionId, String transactionHash) {
        this.userId = userId;
        this.candidateId = candidateId;
        this.electionId = electionId;
        this.transactionHash = transactionHash;
        this.votedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getElectionId() {
        return electionId;
    }

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getVoteHash() {
        return voteHash;
    }

    public void setVoteHash(String voteHash) {
        this.voteHash = voteHash;
    }

    public String getReceiptSignature() {
        return receiptSignature;
    }

    public void setReceiptSignature(String receiptSignature) {
        this.receiptSignature = receiptSignature;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
