package com.todo.projet_springboot.Service;

import com.todo.projet_springboot.DTO.TaskRequest;
import com.todo.projet_springboot.Entity.Task;
import com.todo.projet_springboot.Entity.User;
import com.todo.projet_springboot.Enum.TaskStatus;
import com.todo.projet_springboot.Repository.TaskRepository;
import com.todo.projet_springboot.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new task
    public Task createTask(TaskRequest taskRequest, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setPriorite(taskRequest.getPriorite());
            task.setDeadline(taskRequest.getDeadline());
            task.setStatus(TaskStatus.EN_COURS);
            task.setUser(user.get());
            return taskRepository.save(task);
        }
        return null;
    }

    // Get all tasks for a specific user
    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findByDevid(userId);
    }
    public List<Task> getAllTasksUsers() {
        return taskRepository.findAll();
    }

    // Get a specific task by ID
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    // Update a task
    public Task updateTask(Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(taskRequest.getStatus());
            task.setDeadline(taskRequest.getDeadline());
            return taskRepository.save(task);
        }
        return null;
    }
    public Task affectedTask(Long taskId, TaskRequest taskRequest , Long currentUserId ,String currentUserName) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setDevid(currentUserId);
            task.setDevName(currentUserName);
            return taskRepository.save(task);
        }
        return null;
    }

    // Delete a task
    public boolean deleteTask(Long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }
    public Task markTaskAsDone(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setStatus(TaskStatus.TERMINE);
            return taskRepository.save(task);
        }
        return null;
    }
    public Task markTaskAsQA(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setStatus(TaskStatus.QA);
            return taskRepository.save(task);
        }
        return null;
    }
    public long getDoneTasksCount() {
        return taskRepository.countByStatus(TaskStatus.TERMINE);
    }

    // Count tasks with 'In Progress' or other active statuses
    public long getInProgressTasksCount() {
        return taskRepository.countByStatus(TaskStatus.EN_COURS);
    }
    public long getInQATasksCount() {
        return taskRepository.countByStatus(TaskStatus.QA);
    }
    public long getCountTasks(){
        return taskRepository.count();   }
    public List<Task> getTasksByStatusAndUser(TaskStatus taskStatus, Long userId) {
        return taskRepository.findByStatusAndUserId(taskStatus, userId);
    }
    public List<Task> getTasksByStatus(TaskStatus taskStatus) {
        return taskRepository.findByStatus(taskStatus);
    }
    public List<Task> getTasksDueOnDate(LocalDate date) {
        return taskRepository.findByDeadline(date); // Assuming you have a method that retrieves tasks by deadline
    }

}
