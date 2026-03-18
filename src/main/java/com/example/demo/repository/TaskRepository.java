package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByEmployeeId(Long employeeId);
    
    long countByStatus(String status);
    
    long countByEmployeeId(Long employeeId);
    
    long countByEmployeeIdAndStatus(Long employeeId, String status);
    
    List<Task> findByEmployeeIdAndStatus(Long employeeId, String status);
    
    // 🔴 NEW: Find tasks by deadline and not completed
    List<Task> findByDeadlineAndStatusNot(LocalDate deadline, String status);
    
    // 🔴 NEW: Find tasks by deadline between dates
    List<Task> findByDeadlineBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT new map(t.employeeId as employeeId, COUNT(t) as taskCount) FROM Task t GROUP BY t.employeeId")
    List<Map<String, Object>> getTaskCountByEmployee();
}