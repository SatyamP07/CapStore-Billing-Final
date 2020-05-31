package com.capstore.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CapStoreExceptionHandler {
	
	@ExceptionHandler({InsufficientProductQuantityException.class})
	public ResponseEntity<String> handleError(InsufficientProductQuantityException exception){
		return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({NoProductSelectedException.class})
	public ResponseEntity<String> handleNoProductSelectedException(NoProductSelectedException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(CouponException.class)
	public ResponseEntity<String> handleError(CouponException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
}
