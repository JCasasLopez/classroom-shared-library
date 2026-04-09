package dev.jcasaslopez.classroom.shared.exception;

public class FailedAuthenticationException extends RuntimeException {
	public FailedAuthenticationException(String message) {
        super(message);
	}
}
