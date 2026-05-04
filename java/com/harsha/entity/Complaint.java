package com.harsha.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Complaint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String phoneNumber;
    private String aadhaarNumber;
    private String address;
    private String category;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;
    
    private Integer wardNumber;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    
    // Constructors
    public Complaint() {
    }
    
    public Complaint(Long id, String name, String phoneNumber, String aadhaarNumber, String address, 
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
}