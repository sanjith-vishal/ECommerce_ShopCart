package com.example.demo.exceptions;

public class InsufficientStockException extends RuntimeException{
	public InsufficientStockException(String message) {
        super(message);
    }
}
