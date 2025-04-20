package com.onoff.heatmap.controllers;

import com.onoff.heatmap.controllers.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return new ErrorResponse(String.valueOf(status.value()), message, Instant.now());
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(String.valueOf(status.value()), message, Instant.now(), fieldErrors);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({HttpClientErrorException.NotFound.class})
    public ErrorResponse handleNotFoundException(Exception e) {
        log.error("Not Found Exception: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ErrorResponse handleConflictException(Exception e) {
        log.error("Conflict Exception: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ErrorResponse handleBadRequest(Exception e) {
        log.error("Bad Request Exception: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("Method Not Supported: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed. Please verify your request.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Default message unavailable"),
                        (existing, replacement) -> existing // prevent duplicates
                ));
        log.error("Validation error: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> errors = Map.of(ex.getParameterName(), "Missing required parameter");
        log.error("Missing parameter: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing parameter", errors);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>(ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                )));
        log.error("Constraint Violation: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", errors);
    }
}
