package dev.jcasaslopez.classroom.shared.exception;

@SuppressWarnings("serial")
public class InvalidTokenException extends RuntimeException {
	public InvalidTokenException(String message) {
        super(message);
	}
}
