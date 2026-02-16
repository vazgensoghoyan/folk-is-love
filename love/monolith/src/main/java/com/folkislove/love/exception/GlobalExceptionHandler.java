package com.folkislove.love.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.folkislove.common.dto.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        log.error("Application exception: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .message(ex.getMessage())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse(ex.getMessage());

        log.error("Validation exception: {}", errorMessages);
    
        ErrorResponse response = ErrorResponse.builder()
            .message(errorMessages)
            .build();
    
        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
            .message(ex.getMessage())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        log.error("Unexpected exception", ex);

        ErrorResponse response = ErrorResponse.builder()
            .message("An unexpected error occurred")
            .build();

        return ResponseEntity.internalServerError().body(response);
    }
}