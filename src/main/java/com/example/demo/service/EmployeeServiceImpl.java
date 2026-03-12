package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee login(Employee emp) {
        System.out.println("========== LOGIN SERVICE ==========");
        System.out.println("Email: '" + emp.getEmail() + "'");
        System.out.println("Password: '" + emp.getPassword() + "'");
        
        try {
            // Method 1: Try findByEmailAndPassword first
            System.out.println("Trying findByEmailAndPassword...");
            Employee found = employeeRepository.findByEmailAndPassword(
                emp.getEmail(), 
                emp.getPassword()
            );
            
            if (found != null) {
                System.out.println("✅ Found using findByEmailAndPassword");
                return found;
            }
            
            // Method 2: Try the custom query
            System.out.println("Trying custom query loginUser...");
            found = employeeRepository.loginUser(
                emp.getEmail(), 
                emp.getPassword()
            );
            
            if (found != null) {
                System.out.println("✅ Found using loginUser");
                return found;
            }
            
            // Method 3: Check if email exists at all
            System.out.println("Checking if email exists...");
            Employee byEmail = employeeRepository.findByEmail(emp.getEmail());
            if (byEmail != null) {
                System.out.println("❌ Email exists but password is wrong");
                System.out.println("Stored password: '" + byEmail.getPassword() + "'");
            } else {
                System.out.println("❌ Email not found in database");
            }
            
            return null;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in login service:");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee addEmployee(Employee emp) {
        return employeeRepository.save(emp);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee updateEmployee(Long id, Employee emp) {
        emp.setId(id);
        return employeeRepository.save(emp);
    }
}