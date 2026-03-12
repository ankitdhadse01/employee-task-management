package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Method 1: Using Spring Data JPA naming convention
    Employee findByEmailAndPassword(String email, String password);
    
    // Method 2: Using custom query (more explicit)
    @Query("SELECT e FROM Employee e WHERE e.email = :email AND e.password = :password")
    Employee loginUser(@Param("email") String email, @Param("password") String password);
    
    // Method 3: Find by email only (in case you want to check password separately)
    Employee findByEmail(String email);
}