package com.example.demo.exceptions;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ExceptionResponse {
	private LocalDateTime timestamp;
	private String message;
	private String details;

	public ExceptionResponse(LocalDateTime timestamp, String message, String details) {
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

	// Getters and Setters
}