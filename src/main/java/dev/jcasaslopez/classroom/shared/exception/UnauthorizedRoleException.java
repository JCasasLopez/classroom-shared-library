package dev.jcasaslopez.classroom.shared.exception;

@SuppressWarnings("serial")
public class UnauthorizedRoleException extends RuntimeException {
	public UnauthorizedRoleException(String message) {
        super(message);
	}
}
