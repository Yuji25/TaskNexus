package org.example.tasknexus.controller;

import org.example.tasknexus.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * WelcomeController
 * Handles root endpoint and provides API information
 */
@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    public ResponseEntity<ApiResponse> welcome() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "TaskNexus API");
        info.put("version", "1.0.0");
        info.put("status", "Running");
        info.put("swagger-ui", "/swagger-ui.html");
        info.put("api-docs", "/v3/api-docs");
        info.put("h2-console", "/h2-console");

        return ResponseEntity.ok(ApiResponse.success("Welcome to TaskNexus API", info));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(ApiResponse.success("Application is healthy", "OK"));
    }
}
