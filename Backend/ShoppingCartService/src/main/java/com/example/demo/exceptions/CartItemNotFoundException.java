package com.example.demo.exceptions;

public class CartItemNotFoundException extends RuntimeException {
	public CartItemNotFoundException(String message) {
		super(message);
	}
}