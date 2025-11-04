package com.evoting.swingclient.model;

import java.time.LocalDateTime;

public class Vote {
    private Long id;
    private Long userId;
    private Long candidateId;
    private Long electionId;
    private String transactionHash;
    private Long blockNumber;
    private LocalDateTime votedAt;
    private boolean isVerified;
    private String voteHash;
    private String receiptSignature;
    private String publicKey;
    
    public Vote() {
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
