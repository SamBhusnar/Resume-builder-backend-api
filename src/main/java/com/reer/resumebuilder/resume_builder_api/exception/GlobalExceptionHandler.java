package com.reer.resumebuilder.resume_builder_api.exception;

import com.reer.resumebuilder.resume_builder_api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceExistsException(ResourceExistsException e) {
        log.info("Resource already exists : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder()
                .message(List.of("Resource already exists", e.getMessage()))
                .status(HttpStatus.CONFLICT.toString())
                .timeStamp(System.currentTimeMillis())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(

            MethodArgumentNotValidException ex) {
        log.info("Validation failed");
        Map<String, Object> response = Map.of(
                "message", "Validation failed",
                "status", HttpStatus.BAD_REQUEST.toString(),
                "timeStamp", System.currentTimeMillis(),
                "errors", ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> Map.of(
                                "field", ((FieldError) error).getField(),
                                "rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue() : "No rejected value",
                                "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "No default message"
                        )).toList()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.info("Runtime exception occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .message(List.of(e.getMessage()))
                .status(HttpStatus.BAD_REQUEST.toString())
                .timeStamp(System.currentTimeMillis())
                .build());


    }

    // for internal server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.info("Exception occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                .message(List.of(e.getMessage()))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timeStamp(System.currentTimeMillis())
                .build());


    }

}
