package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();  // ✅ YE IMPLEMENT HONA CHAHIYE
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
    
    @Override
    public Map<String, String> getResourceLinksAsMap(Task task) {
        try {
            if (task.getResourceLinks() != null && !task.getResourceLinks().isEmpty()) {
                return objectMapper.readValue(task.getResourceLinks(), 
                    new TypeReference<Map<String, String>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}