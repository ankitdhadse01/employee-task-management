package com.example.demo.service;
import java.util.List;

import com.example.demo.entity.Employee;

public interface EmployeeService  {

 List<Employee> getAllEmployees();

 Employee addEmployee(Employee e);

 void deleteEmployee(Long id);

 Employee updateEmployee(Long id, Employee e);

 Employee login(Employee e);

}
