 package org.example.tasknexus.service;

import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * FileService
 * Service for handling file uploads and downloads
 */
@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "txt", "jpg", "jpeg", "png", "gif"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Upload file
     */
    public String uploadFile(MultipartFile file, Long taskId) throws IOException {
        log.info("Uploading file for task: {}", taskId);

        // Validate file
        validateFile(file);

        // Create upload directory if not exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "_" + taskId + "." + extension;

        // Save file
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("File uploaded successfully: {}", filename);
        return filename;
    }

    /**
     * Delete file
     */
    public void deleteFile(String filename) throws IOException {
        log.info("Deleting file: {}", filename);

        Path filePath = Paths.get(uploadDir, filename);
        Files.deleteIfExists(filePath);

        log.info("File deleted successfully: {}", filename);
    }

    /**
     * Get file path
     */
    public Path getFilePath(String filename) {
        return Paths.get(uploadDir, filename);
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File size exceeds maximum limit of 10MB");
        }

        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ValidationException("File type not allowed. Allowed types: " + ALLOWED_EXTENSIONS);
        }
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
