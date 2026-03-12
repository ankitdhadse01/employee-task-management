package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getEmployeeTasks(Long employeeId) {
        return taskRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Task createTask(Task task) {
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }
        return taskRepository.save(task);
    }

    @Override
    public Task updateTaskStatus(Long id, String status) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(status);
            return taskRepository.save(task);
        }
        return null;
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task getTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.orElse(null);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }
    
    @Override
    public List<Task> getPendingTasks(Long employeeId) {
        return taskRepository.findByEmployeeIdAndStatus(employeeId, "PENDING");
    }
    
    // 🔴 MANUAL JSON PARSING - NO JACKSON NEEDED
    @Override
    public Map<String, String> getResourceLinksAsMap(Task task) {
        Map<String, String> links = new HashMap<>();
        try {
            String json = task.getResourceLinks();
            if (json != null && !json.isEmpty()) {
                // Remove { } and quotes
                json = json.replace("{", "").replace("}", "").replace("\"", "");
                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        links.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing resource links: " + e.getMessage());
        }
        return links;
    }
}