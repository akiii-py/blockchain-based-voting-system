package com.evoting.swingclient.model;

public class Candidate {
    private Long id;
    private String name;
    private String party;
    private String description;
    private Long electionId;
    private Long voteCount;
    
    public Candidate() {
    }
    
    public Candidate(Long id, String name, String party, String description, Long electionId) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.description = description;
        this.electionId = electionId;
        this.voteCount = 0L;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getParty() {
        return party;
    }
    
    public void setParty(String party) {
        this.party = party;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getElectionId() {
        return electionId;
    }
    
    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }
    
    public Long getVoteCount() {
        return voteCount;
    }
    
    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }
}
