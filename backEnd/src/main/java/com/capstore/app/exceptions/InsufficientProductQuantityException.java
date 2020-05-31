package com.capstore.app.exceptions;

public class InsufficientProductQuantityException extends RuntimeException{

	public InsufficientProductQuantityException(String message) {
		super(message);
	}
}
