package org.example.tasknexus.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.model.Task;
import org.example.tasknexus.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * EmailService
 * Service for sending email notifications
 */
@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * Send welcome email
     */
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            log.info("Sending welcome email to: {}", user.getEmail());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to TaskNexus!");

            String htmlContent = buildWelcomeEmail(user);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    /**
     * Send task created notification
     */
    @Async
    public void sendTaskCreatedEmail(User user, Task task) {
        try {
            log.info("Sending task created email to: {}", user.getEmail());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("New Task Created: " + task.getTitle());

            String htmlContent = buildTaskCreatedEmail(user, task);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Task created email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send task created email: {}", e.getMessage());
        }
    }

    /**
     * Send task completed notification
     */
    @Async
    public void sendTaskCompletedEmail(User user, Task task) {
        try {
            log.info("Sending task completed email to: {}", user.getEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Task Completed: " + task.getTitle());
            message.setText(String.format(
                    "Hi %s,\n\n" +
                    "Congratulations! You have completed the task:\n\n" +
                    "Title: %s\n" +
                    "Priority: %s\n" +
                    "Completed: Just now\n\n" +
                    "Keep up the great work!\n\n" +
                    "Best regards,\n" +
                    "TaskNexus Team",
                    user.getFullName(),
                    task.getTitle(),
                    task.getPriority()
            ));

            mailSender.send(message);
            log.info("Task completed email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send task completed email: {}", e.getMessage());
        }
    }

    /**
     * Send task reminder email
     */
    @Async
    public void sendTaskReminderEmail(User user, Task task) {
        try {
            log.info("Sending task reminder email to: {}", user.getEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Reminder: Task Due Soon - " + task.getTitle());
            message.setText(String.format(
                    "Hi %s,\n\n" +
                    "This is a reminder that your task is due soon:\n\n" +
                    "Title: %s\n" +
                    "Priority: %s\n" +
                    "Due Date: %s\n\n" +
                    "Please complete it on time.\n\n" +
                    "Best regards,\n" +
                    "TaskNexus Team",
                    user.getFullName(),
                    task.getTitle(),
                    task.getPriority(),
                    task.getDueDate() != null ? task.getDueDate().format(formatter) : "Not set"
            ));

            mailSender.send(message);
            log.info("Task reminder email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send task reminder email: {}", e.getMessage());
        }
    }

    /**
     * Build welcome email HTML
     */
    private String buildWelcomeEmail(User user) {
        return String.format(
                "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #4CAF50;'>Welcome to TaskNexus!</h1>" +
                "<p>Hi %s,</p>" +
                "<p>Thank you for joining TaskNexus. We're excited to have you on board!</p>" +
                "<p>With TaskNexus, you can:</p>" +
                "<ul>" +
                "<li>Create and manage tasks efficiently</li>" +
                "<li>Set priorities and due dates</li>" +
                "<li>Track your progress</li>" +
                "<li>Stay organized and productive</li>" +
                "</ul>" +
                "<p>Get started by creating your first task!</p>" +
                "<p>Best regards,<br/>The TaskNexus Team</p>" +
                "</div>" +
                "</body>" +
                "</html>",
                user.getFullName()
        );
    }

    /**
     * Build task created email HTML
     */
    private String buildTaskCreatedEmail(User user, Task task) {
        return String.format(
                "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #2196F3;'>New Task Created</h2>" +
                "<p>Hi %s,</p>" +
                "<p>You have successfully created a new task:</p>" +
                "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px;'>" +
                "<h3 style='margin-top: 0;'>%s</h3>" +
                "<p><strong>Description:</strong> %s</p>" +
                "<p><strong>Priority:</strong> <span style='color: %s;'>%s</span></p>" +
                "<p><strong>Status:</strong> %s</p>" +
                "<p><strong>Due Date:</strong> %s</p>" +
                "</div>" +
                "<p>Good luck with your task!</p>" +
                "<p>Best regards,<br/>TaskNexus Team</p>" +
                "</div>" +
                "</body>" +
                "</html>",
                user.getFullName(),
                task.getTitle(),
                task.getDescription() != null ? task.getDescription() : "No description",
                getPriorityColor(task.getPriority().toString()),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate() != null ? task.getDueDate().format(formatter) : "Not set"
        );
    }

    /**
     * Get priority color
     */
    private String getPriorityColor(String priority) {
        switch (priority) {
            case "HIGH":
            case "URGENT":
                return "#f44336";
            case "MEDIUM":
                return "#ff9800";
            case "LOW":
                return "#4caf50";
            default:
                return "#757575";
        }
    }
}
