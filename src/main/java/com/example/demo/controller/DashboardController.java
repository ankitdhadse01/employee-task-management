package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.TaskRepository;

@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001", "http://localhost:3002",
		"http://localhost:3003", "http://localhost:3004" })
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private TaskRepository taskRepository;

	@GetMapping("/stats")
	public Map<String, Object> getDashboardStats() {
		Map<String, Object> stats = new HashMap<>();

		try {
			// Basic counts
			stats.put("totalEmployees", employeeRepository.count());
			stats.put("totalTasks", taskRepository.count());
			stats.put("completedTasks", taskRepository.countByStatus("COMPLETED"));
			stats.put("pendingTasks", taskRepository.countByStatus("PENDING"));
			stats.put("inProgressTasks", taskRepository.countByStatus("IN_PROGRESS"));

		} catch (Exception e) {
			System.err.println("Error in dashboard stats: " + e.getMessage());
			e.printStackTrace();
		}

		return stats;
	}

	@GetMapping("/employee/{id}")
	public Map<String, Object> getEmployeeDashboard(@PathVariable Long id) {
		Map<String, Object> stats = new HashMap<>();

		try {
			stats.put("myTasks", taskRepository.countByEmployeeId(id));
			stats.put("myCompletedTasks", taskRepository.countByEmployeeIdAndStatus(id, "COMPLETED"));
			stats.put("myPendingTasks", taskRepository.countByEmployeeIdAndStatus(id, "PENDING"));
			stats.put("myInProgressTasks", taskRepository.countByEmployeeIdAndStatus(id, "IN_PROGRESS"));
		} catch (Exception e) {
			System.err.println("Error in employee dashboard: " + e.getMessage());
			e.printStackTrace();
		}

		return stats;
	}
}