package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Task;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.TaskRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    // 📄 Generate Tasks PDF
    public byte[] generateTasksPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Title
        document.add(new Paragraph("Task Report")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        
        document.add(new Paragraph("Generated on: " + LocalDate.now())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        
        document.add(new Paragraph("\n"));

        // Table
        float[] columnWidths = {1, 3, 4, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        String[] headers = {"ID", "Title", "Description", "Employee", "Status", "Priority"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }

        // Data
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            String employeeName = employeeRepository.findById(task.getEmployeeId())
                    .map(Employee::getName)
                    .orElse("Unassigned");
            
            table.addCell(new Cell().add(new Paragraph(String.valueOf(task.getId()))));
            table.addCell(new Cell().add(new Paragraph(task.getTitle())));
            table.addCell(new Cell().add(new Paragraph(task.getDescription() != null ? task.getDescription() : "")));
            table.addCell(new Cell().add(new Paragraph(employeeName)));
            table.addCell(new Cell().add(new Paragraph(task.getStatus() != null ? task.getStatus() : "PENDING")));
            table.addCell(new Cell().add(new Paragraph(task.getPriority() != null ? task.getPriority() : "MEDIUM")));
        }

        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }

    // 📊 Generate Employees Excel
    public byte[] generateEmployeesExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        // Header Style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Headers
        String[] columns = {"ID", "Name", "Email", "Role", "Tasks Count", "Completed Tasks"};
        Row headerRow = sheet.createRow(0);
        
        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        List<Employee> employees = employeeRepository.findAll();
        int rowNum = 1;

        for (Employee emp : employees) {
            Row row = sheet.createRow(rowNum++);
            
            long totalTasks = taskRepository.countByEmployeeId(emp.getId());
            long completedTasks = taskRepository.countByEmployeeIdAndStatus(emp.getId(), "COMPLETED");

            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getName());
            row.createCell(2).setCellValue(emp.getEmail());
            row.createCell(3).setCellValue(emp.getRole());
            row.createCell(4).setCellValue(totalTasks);
            row.createCell(5).setCellValue(completedTasks);
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos.toByteArray();
    }

    // 📧 Send Email Report - FIXED VERSION
    public void sendReportToAdmin(String reportType, byte[] reportData, String fileName) throws Exception {
        List<Employee> admins = employeeRepository.findAll().stream()
                .filter(e -> "ADMIN".equals(e.getRole()))
                .toList();

        for (Employee admin : admins) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(admin.getEmail());
                helper.setSubject("TaskFlow Report: " + reportType + " - " + LocalDate.now());
                
                String content = String.format(
                    "Hello %s,\n\n" +
                    "Please find attached the requested %s report.\n\n" +
                    "Report Details:\n" +
                    "• Type: %s\n" +
                    "• Generated: %s\n" +
                    "• Generated by: System\n\n" +
                    "Regards,\n" +
                    "TaskFlow Team",
                    admin.getName(),
                    reportType,
                    reportType,
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                );
                
                helper.setText(content);
                
                // ✅ FIXED: Convert byte[] to DataSource based on file type
                String contentType = "application/octet-stream";
                
                // ✅ FIXED: endsWith with capital W
                if (fileName.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (fileName.endsWith(".xlsx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                }
                
                // ✅ FIXED: ByteArrayDataSource properly imported
                ByteArrayDataSource dataSource = new ByteArrayDataSource(reportData, contentType);
                helper.addAttachment(fileName, dataSource);
                
                mailSender.send(message);
                System.out.println("✅ Email sent to: " + admin.getEmail());
                
            } catch (Exception e) {
                System.err.println("❌ Error sending email to " + admin.getEmail() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 📊 Generate Task Summary
    public String generateTaskSummary() {
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus("COMPLETED");
        long pendingTasks = taskRepository.countByStatus("PENDING");
        long inProgressTasks = taskRepository.countByStatus("IN_PROGRESS");
        
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;
        
        return String.format(
            "📊 TASK SUMMARY REPORT\n" +
            "======================\n" +
            "Generated: %s\n\n" +
            "Total Tasks: %d\n" +
            "✅ Completed: %d (%.1f%%)\n" +
            "🔄 In Progress: %d\n" +
            "⏳ Pending: %d",
            LocalDate.now(),
            totalTasks,
            completedTasks,
            completionRate,
            inProgressTasks,
            pendingTasks
        );
    }
}