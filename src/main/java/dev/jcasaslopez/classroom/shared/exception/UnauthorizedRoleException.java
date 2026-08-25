package dev.jcasaslopez.classroom.shared.exception;

public class UnauthorizedRoleException extends RuntimeException {
	public UnauthorizedRoleException(String message) {
        super(message);
	}
}
