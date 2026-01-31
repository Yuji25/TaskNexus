package org.example.tasknexus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.dto.ApiResponse;
import org.example.tasknexus.dto.TaskDTO;
import org.example.tasknexus.model.TaskPriority;
import org.example.tasknexus.model.TaskStatus;
import org.example.tasknexus.service.TaskService;
import org.example.tasknexus.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * TaskController
 * REST controller for task-related endpoints
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private FileService fileService;

    /**
     * Create a new task
     * POST /tasks
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> createTask(
            HttpServletRequest request,
            @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Create task endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            TaskDTO createdTask = taskService.createTask(userId, taskDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Task created successfully", createdTask));
        } catch (Exception e) {
            log.error("Create task error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Get all tasks for current user
     * GET /tasks
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getAllTasks(
            HttpServletRequest request,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {
        log.info("Get all tasks endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");

            // If pagination parameters provided, return paginated results
            if (page != null && size != null) {
                Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "ASC"),
                        sortBy != null ? sortBy : "createdAt");
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<TaskDTO> tasks = taskService.getAllTasksPaginated(userId, pageable);
                return ResponseEntity.ok()
                        .body(ApiResponse.success("Tasks fetched successfully", tasks));
            }

            // Otherwise return all tasks
            List<TaskDTO> tasks = taskService.getAllTasks(userId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Tasks fetched successfully", tasks));
        } catch (Exception e) {
            log.error("Get tasks error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get task by ID
     * GET /tasks/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getTaskById(
            HttpServletRequest request,
            @PathVariable Long id) {
        log.info("Get task by ID: {}", id);

        try {
            Long userId = (Long) request.getAttribute("userId");
            TaskDTO task = taskService.getTaskById(userId, id);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Task fetched successfully", task));
        } catch (Exception e) {
            log.error("Get task error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), 404));
        }
    }

    /**
     * Get tasks by status
     * GET /tasks/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getTasksByStatus(
            HttpServletRequest request,
            @PathVariable TaskStatus status) {
        log.info("Get tasks by status: {}", status);

        try {
            Long userId = (Long) request.getAttribute("userId");
            List<TaskDTO> tasks = taskService.getTasksByStatus(userId, status);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Tasks fetched successfully", tasks));
        } catch (Exception e) {
            log.error("Get tasks by status error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get tasks by priority
     * GET /tasks/priority/{priority}
     */
    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getTasksByPriority(
            HttpServletRequest request,
            @PathVariable TaskPriority priority) {
        log.info("Get tasks by priority: {}", priority);

        try {
            Long userId = (Long) request.getAttribute("userId");
            List<TaskDTO> tasks = taskService.getTasksByPriority(userId, priority);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Tasks fetched successfully", tasks));
        } catch (Exception e) {
            log.error("Get tasks by priority error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Search tasks
     * GET /tasks/search?query=text
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> searchTasks(
            HttpServletRequest request,
            @RequestParam String query) {
        log.info("Search tasks with query: {}", query);

        try {
            Long userId = (Long) request.getAttribute("userId");
            List<TaskDTO> tasks = taskService.searchTasks(userId, query);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Tasks fetched successfully", tasks));
        } catch (Exception e) {
            log.error("Search tasks error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get overdue tasks
     * GET /tasks/overdue
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getOverdueTasks(HttpServletRequest request) {
        log.info("Get overdue tasks endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            List<TaskDTO> tasks = taskService.getOverdueTasks(userId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Overdue tasks fetched successfully", tasks));
        } catch (Exception e) {
            log.error("Get overdue tasks error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get tasks due today
     * GET /tasks/due-today
     */
    @GetMapping("/due-today")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getTasksDueToday(HttpServletRequest request) {
        log.info("Get tasks due today endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            List<TaskDTO> tasks = taskService.getTasksDueToday(userId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Tasks due today fetched successfully", tasks));
        } catch (Exception e) {
            log.error("Get tasks due today error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Update task
     * PUT /tasks/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateTask(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Update task endpoint called for ID: {}", id);

        try {
            Long userId = (Long) request.getAttribute("userId");
            TaskDTO updatedTask = taskService.updateTask(userId, id, taskDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Task updated successfully", updatedTask));
        } catch (Exception e) {
            log.error("Update task error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Update task status
     * PATCH /tasks/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateTaskStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody TaskStatusUpdateRequest statusRequest) {
        log.info("Update task status endpoint called for ID: {}", id);

        try {
            Long userId = (Long) request.getAttribute("userId");
            TaskDTO updatedTask = taskService.updateTaskStatus(userId, id, statusRequest.getStatus());
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Task status updated successfully", updatedTask));
        } catch (Exception e) {
            log.error("Update task status error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Delete task
     * DELETE /tasks/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteTask(
            HttpServletRequest request,
            @PathVariable Long id) {
        log.info("Delete task endpoint called for ID: {}", id);

        try {
            Long userId = (Long) request.getAttribute("userId");
            taskService.deleteTask(userId, id);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Task deleted successfully", null));
        } catch (Exception e) {
            log.error("Delete task error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Upload file attachment for task
     * POST /tasks/{id}/upload
     */
    @PostMapping("/{id}/upload")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> uploadAttachment(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        log.info("Upload attachment endpoint called for task: {}", id);

        try {
            Long userId = (Long) request.getAttribute("userId");

            // Verify task belongs to user
            TaskDTO task = taskService.getTaskById(userId, id);

            // Upload file
            String filename = fileService.uploadFile(file, id);

            // Update task with attachment info
            String attachments = task.getAttachments();
            attachments = (attachments == null || attachments.isEmpty())
                    ? filename
                    : attachments + "," + filename;

            task.setAttachments(attachments);
            taskService.updateTask(userId, id, task);

            return ResponseEntity.ok()
                    .body(ApiResponse.success("File uploaded successfully",
                            Map.of("filename", filename, "taskId", id)));
        } catch (Exception e) {
            log.error("Upload attachment error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Download file attachment
     * GET /tasks/{id}/download/{filename}
     */
    @GetMapping("/{id}/download/{filename}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> downloadAttachment(
            HttpServletRequest request,
            @PathVariable Long id,
            @PathVariable String filename) {
        log.info("Download attachment endpoint called: {}", filename);

        try {
            Long userId = (Long) request.getAttribute("userId");

            // Verify task belongs to user
            taskService.getTaskById(userId, id);

            // Get file
            Path filePath = fileService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("File not found", 404));
            }
        } catch (Exception e) {
            log.error("Download attachment error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Task status update request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TaskStatusUpdateRequest {
        private TaskStatus status;
    }
}
