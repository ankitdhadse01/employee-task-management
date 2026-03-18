package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003", "http://localhost:3004"})
public class ProfilePhotoController {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Value("${file.upload.directory:./uploads}")
    private String uploadDir;

    @GetMapping("/photo/{employeeId}")
    public ResponseEntity<?> getProfilePhoto(@PathVariable Long employeeId) {
        try {
            System.out.println("📸 Fetching photo for employee: " + employeeId);
            System.out.println("📁 Upload directory: " + uploadDir);
            
            Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
            if (!employeeOpt.isPresent()) {
                System.out.println("❌ Employee not found: " + employeeId);
                return ResponseEntity.notFound().build();
            }
            
            Employee employee = employeeOpt.get();
            String photoFileName = employee.getProfilePhoto();
            
            if (photoFileName == null || photoFileName.isEmpty()) {
                System.out.println("📭 No photo for employee: " + employeeId);
                return ResponseEntity.notFound().build();
            }
            
            Path photoPath = Paths.get(uploadDir).resolve(photoFileName).normalize();
            System.out.println("📁 Looking for photo at: " + photoPath.toAbsolutePath());
            
            if (!Files.exists(photoPath)) {
                System.out.println("❌ Photo file not found: " + photoPath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            if (!Files.isReadable(photoPath)) {
                System.out.println("❌ Photo file not readable: " + photoPath.toAbsolutePath());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            byte[] image = Files.readAllBytes(photoPath);
            String contentType = Files.probeContentType(photoPath);
            if (contentType == null) {
                contentType = "image/jpeg";
            }
            
            System.out.println("✅ Photo found, size: " + image.length + " bytes, type: " + contentType);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image);
            
        } catch (IOException e) {
            System.err.println("❌ Error reading photo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading photo: " + e.getMessage());
        }
    }

    @PostMapping("/photo/{employeeId}")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            System.out.println("📤 Upload request for employee: " + employeeId);
            System.out.println("📁 File name: " + file.getOriginalFilename());
            System.out.println("📏 File size: " + file.getSize());
            System.out.println("📁 Upload directory: " + uploadDir);
            
            Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
            if (!employeeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Employee employee = employeeOpt.get();
            
            // Delete old photo if exists
            if (employee.getProfilePhoto() != null) {
                try {
                    Path oldPhotoPath = Paths.get(uploadDir).resolve(employee.getProfilePhoto());
                    Files.deleteIfExists(oldPhotoPath);
                    System.out.println("🗑️ Deleted old photo: " + employee.getProfilePhoto());
                } catch (IOException e) {
                    System.err.println("⚠️ Error deleting old photo: " + e.getMessage());
                }
            }
            
            // Save new photo
            String fileName = fileStorageService.storeFile(file);
            employee.setProfilePhoto(fileName);
            employeeRepository.save(employee);
            
            System.out.println("✅ Photo saved: " + fileName);
            
            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("message", "Profile photo uploaded successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error uploading photo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload photo: " + e.getMessage());
        }
    }

    @DeleteMapping("/photo/{employeeId}")
    public ResponseEntity<?> deleteProfilePhoto(@PathVariable Long employeeId) {
        try {
            Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
            if (!employeeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Employee employee = employeeOpt.get();
            
            if (employee.getProfilePhoto() != null) {
                Path photoPath = Paths.get(uploadDir).resolve(employee.getProfilePhoto());
                Files.deleteIfExists(photoPath);
                employee.setProfilePhoto(null);
                employeeRepository.save(employee);
                System.out.println("🗑️ Deleted photo for employee: " + employeeId);
            }
            
            return ResponseEntity.ok("Profile photo deleted successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error deleting photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not delete photo: " + e.getMessage());
        }
    }
}