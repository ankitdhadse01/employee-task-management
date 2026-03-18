package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Task;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.NotificationService;
import com.example.demo.service.TaskService;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003", "http://localhost:3004"})
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private NotificationService notificationService;  // 🔴 ADD THIS

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/employee/{id}")
    public List<Task> getEmployeeTasks(@PathVariable Long id) {
        return taskService.getEmployeeTasks(id);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            Task savedTask = taskService.createTask(task);
            
            // 🔴 Send email notification to employee
            Employee employee = employeeRepository.findById(task.getEmployeeId()).orElse(null);
            if (employee != null) {
                notificationService.sendTaskAssignmentEmail(savedTask, employee);
                System.out.println("📧 Assignment email sent to " + employee.getEmail());
            }
            
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating task: " + e.getMessage());
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTaskStatus(id, task.getStatus());
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating status: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting task: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            if (task != null) {
                return ResponseEntity.ok(task);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching task: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            task.setId(id);
            Task updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating task: " + e.getMessage());
        }
    }
}