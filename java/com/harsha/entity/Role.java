package com.harsha.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")  // Explicitly define table name
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name;
    
    // Default no-arg constructor
    public Role() {
    }
    
    // Constructor with name parameter
    public Role(String name) {
        this.name = name;
    }
    
    // Getters and setters
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
}