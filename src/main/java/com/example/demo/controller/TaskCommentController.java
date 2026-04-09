package com.example.demo.controller;

import com.example.demo.entity.TaskComment;
import com.example.demo.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003", "http://localhost:3004"})
public class TaskCommentController {

    @Autowired
    private TaskCommentService commentService;

    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> addComment(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            String content = request.get("content").toString();
            Long parentCommentId = request.get("parentCommentId") != null ? 
                                   Long.valueOf(request.get("parentCommentId").toString()) : null;
            
            TaskComment comment = commentService.createComment(taskId, employeeId, content, parentCommentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("comment", comment);
            response.put("message", "Comment added successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getComments(@PathVariable Long taskId) {
        try {
            List<TaskComment> comments = commentService.getCommentsByTaskId(taskId);
            long count = commentService.getCommentCount(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("comments", comments);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            String content = request.get("content").toString();
            
            TaskComment comment = commentService.updateComment(commentId, employeeId, content);
            
            if (comment != null) {
                return ResponseEntity.ok(Map.of("success", true, "comment", comment));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "error", "Not authorized to edit this comment"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long employeeId,
            @RequestParam boolean isAdmin) {
        try {
            boolean deleted = commentService.deleteComment(commentId, employeeId, isAdmin);
            if (deleted) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Comment deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "error", "Not authorized to delete this comment"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
