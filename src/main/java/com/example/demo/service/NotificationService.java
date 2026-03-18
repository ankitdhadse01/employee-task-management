package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Task;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.TaskRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    // 📧 Send email when task is assigned
    public void sendTaskAssignmentEmail(Task task, Employee employee) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(employee.getEmail());
        message.setSubject("🔔 New Task Assigned: " + task.getTitle());
        
        String emailBody = String.format(
            "Hello %s,\n\n" +
            "A new task has been assigned to you:\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📋 TASK: %s\n" +
            "📝 DESCRIPTION: %s\n" +
            "📅 DEADLINE: %s\n" +
            "⚡ PRIORITY: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Please log in to your dashboard to view details and start working.\n\n" +
            "Regards,\n" +
            "TaskFlow Team",
            employee.getName(),
            task.getTitle(),
            task.getDescription(),
            task.getDeadline() != null ? task.getDeadline() : "No deadline",
            task.getPriority() != null ? task.getPriority() : "MEDIUM"
        );
        
        message.setText(emailBody);
        mailSender.send(message);
    }

    // 📧 Send deadline reminder email
    public void sendDeadlineReminder(Task task, Employee employee) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(employee.getEmail());
        message.setSubject("⚠️ Task Deadline Tomorrow: " + task.getTitle());
        
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline());
        
        String emailBody = String.format(
            "Hello %s,\n\n" +
            "⏰ REMINDER: Your task deadline is TOMORROW!\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📋 TASK: %s\n" +
            "📝 DESCRIPTION: %s\n" +
            "📅 DEADLINE: %s (%d days left)\n" +
            "⚡ PRIORITY: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Please complete the task soon.\n\n" +
            "Regards,\n" +
            "TaskFlow Team",
            employee.getName(),
            task.getTitle(),
            task.getDescription(),
            task.getDeadline(),
            daysLeft,
            task.getPriority() != null ? task.getPriority() : "MEDIUM"
        );
        
        message.setText(emailBody);
        mailSender.send(message);
    }

    // 🤖 AUTO SCHEDULER: Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void checkDeadlinesAndSendReminders() {
        System.out.println("🔍 Checking deadlines for reminders...");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Task> tasksDueTomorrow = taskRepository.findByDeadlineAndStatusNot(tomorrow, "COMPLETED");
        
        for (Task task : tasksDueTomorrow) {
            Employee employee = employeeRepository.findById(task.getEmployeeId()).orElse(null);
            if (employee != null) {
                sendDeadlineReminder(task, employee);
                System.out.println("📧 Reminder sent to " + employee.getEmail() + " for task: " + task.getTitle());
            }
        }
    }
}