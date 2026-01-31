package org.example.tasknexus.service;

import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.dto.TaskDTO;
import org.example.tasknexus.exception.ResourceNotFoundException;
import org.example.tasknexus.exception.ValidationException;
import org.example.tasknexus.model.Task;
import org.example.tasknexus.model.TaskPriority;
import org.example.tasknexus.model.TaskStatus;
import org.example.tasknexus.repository.TaskRepository;
import org.example.tasknexus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskService
 * Service layer for task-related operations
 */
@Slf4j
@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Create a new task
     */
    public TaskDTO createTask(Long userId, TaskDTO taskDTO) {
        log.info("Creating task for user: {}", userId);

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.PENDING);
        task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(taskDTO.getDueDate());
        task.setUserId(userId);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully: {}", savedTask.getId());

        // Send task created email
        try {
            emailService.sendTaskCreatedEmail(user, savedTask);
        } catch (Exception e) {
            log.error("Failed to send task created email: {}", e.getMessage());
        }

        return TaskDTO.fromEntity(savedTask);
    }

    /**
     * Get all tasks for a user
     */
    public List<TaskDTO> getAllTasks(Long userId) {
        log.info("Fetching all tasks for user: {}", userId);

        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks for a user with pagination
     */
    public Page<TaskDTO> getAllTasksPaginated(Long userId, Pageable pageable) {
        log.info("Fetching paginated tasks for user: {}", userId);

        Page<Task> tasks = taskRepository.findByUserId(userId, pageable);
        return tasks.map(TaskDTO::fromEntity);
    }

    /**
     * Get task by ID
     */
    public TaskDTO getTaskById(Long userId, Long taskId) {
        log.info("Fetching task: {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            throw new ValidationException("Task does not belong to this user");
        }

        return TaskDTO.fromEntity(task);
    }

    /**
     * Get tasks by status
     */
    public List<TaskDTO> getTasksByStatus(Long userId, TaskStatus status) {
        log.info("Fetching tasks with status {} for user: {}", status, userId);

        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by priority
     */
    public List<TaskDTO> getTasksByPriority(Long userId, TaskPriority priority) {
        log.info("Fetching tasks with priority {} for user: {}", priority, userId);

        List<Task> tasks = taskRepository.findByUserIdAndPriority(userId, priority);
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Search tasks by title or description
     */
    public List<TaskDTO> searchTasks(Long userId, String query) {
        log.info("Searching tasks for user {} with query: {}", userId, query);

        List<Task> tasks = taskRepository.findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
                userId, query, userId, query);
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update task
     */
    public TaskDTO updateTask(Long userId, Long taskId, TaskDTO taskDTO) {
        log.info("Updating task: {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            throw new ValidationException("Task does not belong to this user");
        }

        // Update fields
        if (taskDTO.getTitle() != null) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        if (taskDTO.getDueDate() != null) {
            task.setDueDate(taskDTO.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", updatedTask.getId());

        return TaskDTO.fromEntity(updatedTask);
    }

    /**
     * Update task status
     */
    public TaskDTO updateTaskStatus(Long userId, Long taskId, TaskStatus status) {
        log.info("Updating task status: {} to {} for user: {}", taskId, status, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            throw new ValidationException("Task does not belong to this user");
        }

        task.setStatus(status);

        // If marked as completed, set completion date
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());

            // Send completion email
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                emailService.sendTaskCompletedEmail(user, task);
            } catch (Exception e) {
                log.error("Failed to send task completed email: {}", e.getMessage());
            }
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task status updated successfully: {}", updatedTask.getId());

        return TaskDTO.fromEntity(updatedTask);
    }

    /**
     * Delete task
     */
    public void deleteTask(Long userId, Long taskId) {
        log.info("Deleting task: {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            throw new ValidationException("Task does not belong to this user");
        }

        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", taskId);
    }

    /**
     * Get overdue tasks
     */
    public List<TaskDTO> getOverdueTasks(Long userId) {
        log.info("Fetching overdue tasks for user: {}", userId);

        List<Task> tasks = taskRepository.findByUserIdAndDueDateBeforeAndStatusNot(
                userId, LocalDateTime.now(), TaskStatus.COMPLETED);
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks due today
     */
    public List<TaskDTO> getTasksDueToday(Long userId) {
        log.info("Fetching tasks due today for user: {}", userId);

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        List<Task> tasks = taskRepository.findByUserIdAndDueDateBetween(userId, startOfDay, endOfDay);
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
