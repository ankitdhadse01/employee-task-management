package com.example.demo.service;

import java.util.List;
import java.util.Map;
import com.example.demo.entity.Task;

public interface TaskService {
    
    // Basic CRUD operations
    List<Task> getAllTasks();
    List<Task> getEmployeeTasks(Long employeeId);
    Task createTask(Task task);
    Task updateTaskStatus(Long id, String status);
    void deleteTask(Long id);
    Task getTaskById(Long id);
    Task updateTask(Task task);
    
    // 🔴 NEW: Get pending tasks for an employee
    List<Task> getPendingTasks(Long employeeId);
    
    // 🔴 NEW: Parse resource links from JSON to Map
    Map<String, String> getResourceLinksAsMap(Task task);
}