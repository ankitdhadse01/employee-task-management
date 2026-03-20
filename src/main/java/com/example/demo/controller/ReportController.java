package com.example.demo.controller;

import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003", "http://localhost:3004"})
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/tasks/pdf")
    public ResponseEntity<?> downloadTasksPdf() {
        try {
            byte[] pdfData = reportService.generateTasksPdf();
            
            String fileName = "task_report_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfData);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

    @GetMapping("/employees/excel")
    public ResponseEntity<?> downloadEmployeesExcel() {
        try {
            byte[] excelData = reportService.generateEmployeesExcel();
            
            String fileName = "employee_report_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating Excel: " + e.getMessage());
        }
    }

    @PostMapping("/email/pdf")
    public ResponseEntity<?> emailTasksPdf() {
        try {
            byte[] pdfData = reportService.generateTasksPdf();
            String fileName = "task_report_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            
            reportService.sendReportToAdmin("Tasks PDF", pdfData, fileName);
            
            return ResponseEntity.ok("Task report sent to admin emails successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/email/excel")
    public ResponseEntity<?> emailEmployeesExcel() {
        try {
            byte[] excelData = reportService.generateEmployeesExcel();
            String fileName = "employee_report_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            
            reportService.sendReportToAdmin("Employees Excel", excelData, fileName);
            
            return ResponseEntity.ok("Employee report sent to admin emails successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getTaskSummary() {
        try {
            String summary = reportService.generateTaskSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating summary: " + e.getMessage());
        }
    }
}