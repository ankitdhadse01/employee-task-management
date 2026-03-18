package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

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
    
    @Column(name = "resource_links", columnDefinition = "TEXT")
    private String resourceLinks;
    
    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;
    
    @Column(name = "deadline")
    private LocalDate deadline;
    
    private String priority; // HIGH, MEDIUM, LOW

    // Constructors
    public Task() {}

    public Task(String title, String description, String status, Long employeeId, 
                String resourceLinks, String attachments, LocalDate deadline, String priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.employeeId = employeeId;
        this.resourceLinks = resourceLinks;
        this.attachments = attachments;
        this.deadline = deadline;
        this.priority = priority;
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

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}