package com.onoff.heatmap.controllers;

import com.onoff.heatmap.controllers.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return new ErrorResponse(String.valueOf(status.value()), message, Instant.now());
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(String.valueOf(status.value()), message, Instant.now(), fieldErrors);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({HttpClientErrorException.NotFound.class})
    public ErrorResponse handleNotFoundException(Exception e) {
        logger.error("Not Found Exception: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ErrorResponse handleConflictException(Exception e) {
        logger.error("Conflict Exception: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ErrorResponse handleBadRequest(Exception e) {
        logger.error("Bad Request Exception: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.error("Method Not Supported: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed. Please verify your request.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        logger.error("Validation Exception: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ErrorResponse handleAllExceptions(Exception ex) {
        logger.error("Internal Server Error: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ));
        logger.error("Constraint Violation: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", errors);
    }
}
