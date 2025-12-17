package com.example.cloudBalance.cloudBalance.exception;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException ex) {

        ApiResponse<?> response = ApiResponse.error(
                ex.getMessage(),
                ex.getErrorCode(),
                ex.getStatus().value()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }


        // Validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        ApiResponse<?> response = ApiResponse.error(
                errors.toString(),
                ErrorCode.INVALID_CREDENTIALS,
                400
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {

        ApiResponse<?> response = ApiResponse.error(
                "Internal server error",
                ErrorCode.INTERNAL_ERROR,
                500
        );

        return ResponseEntity.internalServerError().body(response);
    }


}
