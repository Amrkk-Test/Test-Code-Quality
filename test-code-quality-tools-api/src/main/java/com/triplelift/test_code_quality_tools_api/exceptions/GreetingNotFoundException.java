package com.triplelift.test_code_quality_tools_api.exceptions;

public class GreetingNotFoundException extends RuntimeException {
	public GreetingNotFoundException(String message) {
		super(message);
	}
}
