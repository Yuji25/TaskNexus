package org.example.tasknexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse
 * Generic response wrapper for all API endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private String status;
    private String message;
    private Object data;
    private Integer code;
    private Long timestamp;

    /**
     * Constructor without timestamp
     */
    public ApiResponse(String status, String message, Object data, Integer code) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = code;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Success response
     */
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse("success", message, data, 200);
    }

    /**
     * Error response
     */
    public static ApiResponse error(String message, Integer code) {
        return new ApiResponse("error", message, null, code);
    }

    /**
     * Validation error response
     */
    public static ApiResponse validationError(String message, Object errors) {
        return new ApiResponse("validation_error", message, errors, 400);
    }
}
