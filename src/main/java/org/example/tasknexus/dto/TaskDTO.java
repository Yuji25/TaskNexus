package org.example.tasknexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasknexus.model.Task;
import org.example.tasknexus.model.TaskStatus;
import org.example.tasknexus.model.TaskPriority;

import java.time.LocalDateTime;

/**
 * TaskDTO
 * Data Transfer Object for Task entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long userId;
    private LocalDateTime dueDate;
    private String tags;
    private String attachments;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;

    /**
     * Convert Task entity to TaskDTO
     */
    public static TaskDTO fromEntity(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setUserId(task.getUserId());
        dto.setDueDate(task.getDueDate());
        dto.setTags(task.getTags());
        dto.setAttachments(task.getAttachments());
        dto.setIsCompleted(task.getIsCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setNotes(task.getNotes());

        return dto;
    }

    /**
     * Convert TaskDTO to Task entity
     */
    public Task toEntity() {
        Task task = new Task();
        task.setId(this.id);
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setStatus(this.status != null ? this.status : TaskStatus.PENDING);
        task.setPriority(this.priority != null ? this.priority : TaskPriority.MEDIUM);
        task.setUserId(this.userId);
        task.setDueDate(this.dueDate);
        task.setTags(this.tags);
        task.setAttachments(this.attachments);
        task.setIsCompleted(this.isCompleted != null ? this.isCompleted : false);
        task.setNotes(this.notes);

        return task;
    }
}
