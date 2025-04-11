package com.cordilleracoffee.product.infrastructure.api.controller;

import com.cordilleracoffee.product.application.exception.UnauthorizedUserException;
import com.cordilleracoffee.product.infrastructure.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApplicationControllerAdvice {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {


        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((first, second) -> first + ", " + second)
                .orElse("Validation error");

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(LocalDateTime.now(), "PRD-VA-01", errors,
                        ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String error = ex.getName() + " cannot be parsed properly";

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(LocalDateTime.now(), "PRD-VA-02", error,
                        ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));
    }


    @ExceptionHandler(MissingRequestValueException.class)
    ResponseEntity<ApiErrorResponse> handleMissingRequestValueException(MissingRequestValueException ex) {

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(LocalDateTime.now(), "PRD-VA-03", ex.getMessage(),
                        ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    ResponseEntity<ApiErrorResponse> handleUnauthorizedUserException(UnauthorizedUserException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(LocalDateTime.now(), "PRD-VA-04", ex.getMessage(),
                        ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString()));
    }
}
