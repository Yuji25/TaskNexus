package org.example.tasknexus.repository;

import org.example.tasknexus.model.Task;
import org.example.tasknexus.model.TaskStatus;
import org.example.tasknexus.model.TaskPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskRepository
 * Data access layer for Task entity
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority);

    List<Task> findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
            Long userId1, String title, Long userId2, String description);

    List<Task> findByUserIdAndDueDateBeforeAndStatusNot(Long userId, LocalDateTime date, TaskStatus status);

    List<Task> findByUserIdAndDueDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND " +
           "(t.status = :status OR :status IS NULL) AND " +
           "(t.priority = :priority OR :priority IS NULL) AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL) " +
           "ORDER BY t.createdAt DESC")
    Page<Task> findTasksWithFilters(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.dueDate ASC")
    List<Task> findTasksByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate < CURRENT_TIMESTAMP AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("userId") Long userId);

    Page<Task> findByStatusAndUserId(TaskStatus status, Long userId, Pageable pageable);

    Long countByUserIdAndStatus(Long userId, TaskStatus status);
}
