package com.harsha.dto;

import com.harsha.entity.ComplaintStatus;
import java.time.LocalDateTime;

public class ComplaintDTO {
    
    private Long id;
    private String name;
    private String phoneNumber;
    private String aadhaarNumber;
    private String address;
    private String category;
    private String description;
    private ComplaintStatus status;
    private Integer wardNumber;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    
    // Constructors
    public ComplaintDTO() {
    }
    
    public ComplaintDTO(Long id, String name, String phoneNumber, String aadhaarNumber, String address, 
                        String category, String description, ComplaintStatus status, Integer wardNumber, 
                        LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.aadhaarNumber = aadhaarNumber;
        this.address = address;
        this.category = category;
        this.description = description;
        this.status = status;
        this.wardNumber = wardNumber;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
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
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }
    
    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ComplaintStatus getStatus() {
        return status;
    }
    
    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }
    
    public Integer getWardNumber() {
        return wardNumber;
    }
    
    public void setWardNumber(Integer wardNumber) {
        this.wardNumber = wardNumber;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private String name;
        private String phoneNumber;
        private String aadhaarNumber;
        private String address;
        private String category;
        private String description;
        private ComplaintStatus status;
        private Integer wardNumber;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        
        public Builder aadhaarNumber(String aadhaarNumber) {
            this.aadhaarNumber = aadhaarNumber;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder category(String category) {
            this.category = category;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder status(ComplaintStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder wardNumber(Integer wardNumber) {
            this.wardNumber = wardNumber;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }
        
        public ComplaintDTO build() {
            ComplaintDTO dto = new ComplaintDTO();
            dto.setId(this.id);
            dto.setName(this.name);
            dto.setPhoneNumber(this.phoneNumber);
            dto.setAadhaarNumber(this.aadhaarNumber);
            dto.setAddress(this.address);
            dto.setCategory(this.category);
            dto.setDescription(this.description);
            dto.setStatus(this.status);
            dto.setWardNumber(this.wardNumber);
            dto.setCreatedAt(this.createdAt);
            dto.setResolvedAt(this.resolvedAt);
            return dto;
        }
    }
}