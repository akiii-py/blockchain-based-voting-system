package com.evoting.swingclient.model;

import com.google.gson.annotations.SerializedName;

public class Election {
    private Long id;
    private String title;
    private String description;
    // time-window removed from backend; keep model minimal
    @SerializedName("active")
    private boolean active;
    
    public Election() {
    }
    
    public Election(Long id, String title, String description, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.active = active;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
