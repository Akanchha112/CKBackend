package com.example.cloudBalance.cloudBalance.DTO;

import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String message,
        ErrorCode errorCode,
        T data,
        int statusCode,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(String message, T data, int statusCode) {
        return new ApiResponse<>(
                true,
                message,
                null,
                data,
                statusCode,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> error(String message, ErrorCode errorCode, int statusCode) {
        return new ApiResponse<>(
                false,
                message,
                errorCode,
                null,
                statusCode,
                LocalDateTime.now()
        );
    }
}
