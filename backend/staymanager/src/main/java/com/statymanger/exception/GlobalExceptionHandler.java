package com.statymanger.exception;

import com.statymanger.dto.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<Response> handleBookingError(InvalidBookingException ex) {
        return ResponseEntity.status(400).body(
                Response.builder()
                        .statusCode(400)
                        .message("Booking Denied: " + ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleResourceNotFound(ResourceNotFoundException e) {
        ResponseMessage message = new ResponseMessage(HttpStatus.NOT_FOUND.value(), e.getMessage(), Instant.now());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> handleBadCredentials(BadCredentialsException e) {
        Response response = new Response();
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UserNameNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleUserNameNotFound(UserNameNotFoundException e) {
        ResponseMessage message = new ResponseMessage(HttpStatus.NOT_FOUND.value(), e.getMessage(), Instant.now());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles @Version conflicts (Optimistic Locking)
     */
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ResponseMessage> handleOptimisticLocking(Exception e) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.CONFLICT.value(),
                "The data was updated by another user. Please refresh and try again.",
                Instant.now()
        );
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseMessage> handleConstraintViolation(Exception e) {
        ResponseMessage message = new ResponseMessage(HttpStatus.CONFLICT.value(), "This action cannot be completed because it conflicts with existing data.", Instant.now());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Response> handleFileError(FileStorageException ex) {
        return ResponseEntity.status(500).body(
                Response.builder()
                        .statusCode(500)
                        .message("Storage Error: " + ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(StayManagerException.class)
    public ResponseEntity<Response> handleGenericBusinessError(StayManagerException ex) {
        return ResponseEntity.status(500).body(
                Response.builder()
                        .statusCode(500)
                        .message("Business Logic Error: " + ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleGeneralException(Exception e) {
        ResponseMessage message = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), Instant.now());
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public record ResponseMessage(int statusCode, String message, Instant timestamp) {
    }
}