package com.example.demo.repository;

import com.example.demo.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    
    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
    
    List<TaskComment> findByTaskIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long taskId);
    
    List<TaskComment> findByParentCommentId(Long parentCommentId);
    
    @Query("SELECT c FROM TaskComment c WHERE c.taskId = :taskId AND c.parentCommentId IS NULL ORDER BY c.createdAt DESC")
    List<TaskComment> findRootCommentsByTaskId(@Param("taskId") Long taskId);
    
    long countByTaskId(Long taskId);
}
