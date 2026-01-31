package org.example.tasknexus.model;

/**
 * TaskStatus enum for task workflow states
 * PENDING: Task not started yet
 * IN_PROGRESS: Task is being worked on
 * COMPLETED: Task is finished
 * CANCELLED: Task is cancelled
 */
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
