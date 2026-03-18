package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTaskAssignmentEmail(String toEmail, String taskTitle, String employeeName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Task Assigned: " + taskTitle);
        message.setText(String.format(
            "Hello %s,\n\n" +
            "A new task has been assigned to you:\n\n" +
            "Task: %s\n\n" +
            "Please log in to your dashboard to view details.\n\n" +
            "Regards,\nTaskFlow Team",
            employeeName, taskTitle
        ));
        
        mailSender.send(message);
    }

    public void sendTaskCompletionEmail(String toEmail, String taskTitle, String adminName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Task Completed: " + taskTitle);
        message.setText(String.format(
            "Hello %s,\n\n" +
            "Task has been marked as completed:\n\n" +
            "Task: %s\n\n" +
            "Please review in your dashboard.\n\n" +
            "Regards,\nTaskFlow Team",
            adminName, taskTitle
        ));
        
        mailSender.send(message);
    }

    public void sendDeadlineReminder(String toEmail, String taskTitle, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Task Deadline Reminder: " + taskTitle);
        message.setText(String.format(
            "Hello,\n\n" +
            "This is a reminder that task '%s' is due on %s.\n\n" +
            "Please complete it soon.\n\n" +
            "Regards,\nTaskFlow Team",
            taskTitle, dueDate
        ));
        
        mailSender.send(message);
    }
}