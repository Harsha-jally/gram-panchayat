package com.harsha.dto;

import java.time.LocalDateTime;

public class ReportDTO {
    
    private Long id;
    private String title;
    private String description;
    private String imageFileName;
    private LocalDateTime createdAt;
    
    // Constructors
    public ReportDTO() {
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
    
    public String getImageFileName() {
        return imageFileName;
    }
    
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}