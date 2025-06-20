package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomGlobalExceptionHandler {

	@ExceptionHandler(ProductNotFound.class)
	public ResponseEntity<ExceptionResponse> handleProductNotFound(ProductNotFound ex, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(ex.getMessage(), request.getDescription(false),
				LocalDateTime.now());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex,
			WebRequest request) {
		String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		ExceptionResponse response = new ExceptionResponse(errorMessage, request.getDescription(false),
				LocalDateTime.now());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(ex.getMessage(), request.getDescription(false),
				LocalDateTime.now());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		ExceptionResponse response = new ExceptionResponse("Invalid input type: " + ex.getMessage(),
				request.getDescription(false), LocalDateTime.now());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGlobalException(Exception ex, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(ex.getMessage(), request.getDescription(false),
				LocalDateTime.now());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}