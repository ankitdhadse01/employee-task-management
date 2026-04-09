package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.TaskComment;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.TaskCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TaskCommentService {

    @Autowired
    private TaskCommentRepository commentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    // Extract mentions from content (@username)
    public List<String> extractMentions(String content) {
        List<String> mentions = new ArrayList<>();
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        return mentions;
    }

    // Create a new comment
    @Transactional
    public TaskComment createComment(Long taskId, Long employeeId, String content, Long parentCommentId) {
        List<String> mentionedUsers = extractMentions(content);
        String mentionsJson = mentionedUsers.isEmpty() ? null : String.join(",", mentionedUsers);
        
        TaskComment comment = new TaskComment(taskId, employeeId, parentCommentId, content, mentionsJson);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    // Get all comments for a task with employee details
    public List<TaskComment> getCommentsByTaskId(Long taskId) {
        List<TaskComment> comments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        
        // Fetch employee details for each comment
        for (TaskComment comment : comments) {
            Employee employee = employeeRepository.findById(comment.getEmployeeId()).orElse(null);
            if (employee != null) {
                comment.setEmployeeName(employee.getName());
                comment.setEmployeeEmail(employee.getEmail());
                comment.setEmployeePhoto(employee.getProfilePhoto());
            }
        }
        
        // Organize into parent-child hierarchy
        return buildCommentTree(comments);
    }

    // Build threaded comment structure
    private List<TaskComment> buildCommentTree(List<TaskComment> comments) {
        Map<Long, TaskComment> commentMap = new HashMap<>();
        List<TaskComment> rootComments = new ArrayList<>();
        
        for (TaskComment comment : comments) {
            commentMap.put(comment.getId(), comment);
            comment.setReplies(new ArrayList<>());
        }
        
        for (TaskComment comment : comments) {
            if (comment.getParentCommentId() == null) {
                rootComments.add(comment);
            } else {
                TaskComment parent = commentMap.get(comment.getParentCommentId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            }
        }
        
        return rootComments;
    }

    // Update a comment
    @Transactional
    public TaskComment updateComment(Long commentId, Long employeeId, String content) {
        TaskComment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null && comment.getEmployeeId().equals(employeeId)) {
            comment.setContent(content);
            comment.setUpdatedAt(LocalDateTime.now());
            
            List<String> mentionedUsers = extractMentions(content);
            comment.setMentions(mentionedUsers.isEmpty() ? null : String.join(",", mentionedUsers));
            
            return commentRepository.save(comment);
        }
        return null;
    }

    // Delete a comment (and its replies)
    @Transactional
    public boolean deleteComment(Long commentId, Long employeeId, boolean isAdmin) {
        TaskComment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null && (comment.getEmployeeId().equals(employeeId) || isAdmin)) {
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

    // Get comment count for a task
    public long getCommentCount(Long taskId) {
        return commentRepository.countByTaskId(taskId);
    }
}
