package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003", "http://localhost:3004"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            System.out.println("📥 Login request received");
            
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
            
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            
            Employee emp = new Employee();
            emp.setEmail(email);
            emp.setPassword(password);
            
            Employee authenticatedUser = employeeService.login(emp);
            
            if (authenticatedUser != null) {
                System.out.println("✅ Login successful for: " + authenticatedUser.getEmail());
                // Don't send password back to frontend
                authenticatedUser.setPassword(null);
                return ResponseEntity.ok(authenticatedUser);
            } else {
                System.out.println("❌ Login failed: Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }
        } catch (Exception e) {
            System.out.println("❌ Error in login endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            // Don't send passwords
            employees.forEach(emp -> emp.setPassword(null));
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody Employee emp) {
        try {
            Employee savedEmployee = employeeService.addEmployee(emp);
            savedEmployee.setPassword(null);
            return ResponseEntity.ok(savedEmployee);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting employee: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee emp) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, emp);
            if (updatedEmployee != null) {
                updatedEmployee.setPassword(null);
                return ResponseEntity.ok(updatedEmployee);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating employee: " + e.getMessage());
        }
    }
}