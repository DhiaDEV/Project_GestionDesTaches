package com.todo.projet_springboot.Repository;

import com.todo.projet_springboot.Entity.Task;
import com.todo.projet_springboot.Enum.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);  // Find tasks by user ID
    long countByStatus(TaskStatus status);
    List<Task> findByStatusAndUserId(TaskStatus status, Long userId);
    List<Task> findByDeadline(LocalDate date);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByDevid(Long id);
}
