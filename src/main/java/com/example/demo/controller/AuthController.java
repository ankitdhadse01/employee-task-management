package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;

@CrossOrigin
@RestController
@RequestMapping("/api")

public class AuthController {

 @Autowired
 EmployeeService service;

 @PostMapping("/login")
 public Employee login(@RequestBody Employee e){
  return service.login(e);
 }

}
