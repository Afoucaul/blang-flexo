package org.blangflexo.core;

public class ApiAbstractorException extends Exception {
	public ApiAbstractorException(String message) {
		super(message);
	}
	
	public ApiAbstractorException(Exception baseException) {
		super(baseException);
	}
}
