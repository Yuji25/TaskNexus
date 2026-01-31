package org.example.tasknexus.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.dto.ApiResponse;
import org.example.tasknexus.model.TaskStatus;
import org.example.tasknexus.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AnalyticsController
 * REST controller for analytics and reports
 */
@Slf4j
@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Get dashboard statistics
     * GET /analytics/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardStats(HttpServletRequest request) {
        log.info("Get dashboard stats endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");

            Map<String, Object> stats = new HashMap<>();

            // Total tasks
            Long totalTasks = taskRepository.count();
            stats.put("totalTasks", totalTasks);

            // Tasks by status
            Long pendingTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
            Long inProgressTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
            Long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
            Long cancelledTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.CANCELLED);

            stats.put("pendingTasks", pendingTasks);
            stats.put("inProgressTasks", inProgressTasks);
            stats.put("completedTasks", completedTasks);
            stats.put("cancelledTasks", cancelledTasks);

            // Completion rate
            double completionRate = totalTasks > 0
                    ? (completedTasks.doubleValue() / totalTasks.doubleValue()) * 100
                    : 0.0;
            stats.put("completionRate", String.format("%.2f%%", completionRate));

            // Tasks by priority
            Map<String, Long> tasksByPriority = new HashMap<>();
            tasksByPriority.put("HIGH", taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING));
            tasksByPriority.put("MEDIUM", inProgressTasks);
            tasksByPriority.put("LOW", completedTasks);
            stats.put("tasksByPriority", tasksByPriority);

            // Tasks by status
            Map<String, Long> tasksByStatus = new HashMap<>();
            tasksByStatus.put("PENDING", pendingTasks);
            tasksByStatus.put("IN_PROGRESS", inProgressTasks);
            tasksByStatus.put("COMPLETED", completedTasks);
            tasksByStatus.put("CANCELLED", cancelledTasks);
            stats.put("tasksByStatus", tasksByStatus);

            return ResponseEntity.ok()
                    .body(ApiResponse.success("Dashboard stats fetched successfully", stats));
        } catch (Exception e) {
            log.error("Get dashboard stats error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get task summary
     * GET /analytics/summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getTaskSummary(HttpServletRequest request) {
        log.info("Get task summary endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");

            Map<String, Object> summary = new HashMap<>();

            int totalTasks = taskRepository.findByUserId(userId).size();
            long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
            long pendingTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);

            summary.put("totalTasks", totalTasks);
            summary.put("completedTasks", completedTasks);
            summary.put("pendingTasks", pendingTasks);
            summary.put("productivity", totalTasks > 0
                    ? String.format("%.1f%%", ((double) completedTasks / totalTasks) * 100)
                    : "0.0%");

            return ResponseEntity.ok()
                    .body(ApiResponse.success("Task summary fetched successfully", summary));
        } catch (Exception e) {
            log.error("Get task summary error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get user performance metrics
     * GET /analytics/performance
     */
    @GetMapping("/performance")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getPerformanceMetrics(HttpServletRequest request) {
        log.info("Get performance metrics endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");

            Map<String, Object> performance = new HashMap<>();

            int totalTasks = taskRepository.findByUserId(userId).size();
            long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
            int overdueTasks = taskRepository.findOverdueTasks(userId).size();

            performance.put("totalTasks", totalTasks);
            performance.put("completedTasks", completedTasks);
            performance.put("overdueTasks", overdueTasks);
            performance.put("onTimeCompletionRate", totalTasks > 0
                    ? String.format("%.1f%%", ((double) (totalTasks - overdueTasks) / totalTasks) * 100)
                    : "100.0%");
            performance.put("efficiency", completedTasks > 0 ? "Good" : "Needs Improvement");

            return ResponseEntity.ok()
                    .body(ApiResponse.success("Performance metrics fetched successfully", performance));
        } catch (Exception e) {
            log.error("Get performance metrics error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }
}
