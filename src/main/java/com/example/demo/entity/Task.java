package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    
    @Column(name = "employee_id")
    private Long employeeId;
    
    // 🔴 NEW FIELD: Store multiple links as JSON string
    @Column(name = "resource_links", columnDefinition = "TEXT")
    private String resourceLinks; // Format: {"github":"url","vscode":"url"}

    // Constructors
    public Task() {}

    public Task(String title, String description, String status, Long employeeId, String resourceLinks) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.employeeId = employeeId;
        this.resourceLinks = resourceLinks;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getResourceLinks() { return resourceLinks; }
    public void setResourceLinks(String resourceLinks) { this.resourceLinks = resourceLinks; }
}