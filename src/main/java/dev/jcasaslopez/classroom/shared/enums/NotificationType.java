package dev.jcasaslopez.classroom.shared.enums;

public enum NotificationType {

	// Enum fields represent:
	// 1. Email subject line
	// 3. Core HTML message content (excluding greeting and farewell)

	VERIFY_EMAIL(
			"Email verification",
			"""
			<p>Thank you for registering. To complete your account setup,
			please verify your email address by clicking on the following link:</p>
			<p><a href="%s">Verify my email</a></p>
			"""
			),

	CREATE_ACCOUNT(
			"Welcome to the platform!",
			"""
			<p>Your account has been created successfully.</p>
			"""
			),

	REGISTRATION_ATTEMPT(
			"Registration attempt with your email",
			"""
			<p>Someone tried to create a new account using this email address,
			but an account with this email already exists.</p>
			<p>If it was you, you can log in with your existing account,
			or use the "Forgot password" option on the login page if you don't remember it.</p>
			<p>If it wasn't you, you can safely ignore this email. Your account remains secure.</p>
			"""
			),

	FORGOT_PASSWORD(
			"Password reset verification email",
			"""
			<p>Click on the following link to reset your password:</p>
			<p><a href="%s">Reset my password</a></p>
			<p>If you didn't request a password reset, you can safely ignore this email.
			Your password will remain unchanged.</p>
			"""
			),

	RESET_PASSWORD(
			"Password reset successfully",
			"""
			<p>Your password has been reset successfully.</p>
			"""
			),

	CHANGE_PASSWORD(
			"Password changed successfully",
			"""
			<p>Your password has been changed successfully.</p>
			"""
			),

	UPDATE_ACCOUNT_STATUS(
			"Change in account status",
			// The message core is defined in NotificationService and its content depends on 
			// the status the user account has changed to
					"""
					<p>%s</p>
					"""
			),

	BOOKING_CONFIRMED(
			"Booking Confirmed",
			"""
			<p>We are pleased to confirm your booking for classroom %s on the %s from %s to %s.</p>
			"""
			),

	BOOKING_CANCELLED(
			"Booking cancelled successfully",
			"""
			<p>We are pleased to confirm that your booking for classroom %s on the %s from %s to %s has been succesfully cancelled.</p>
			"""
			),

	WATCH_ALERT_CONFIRMED(
			"Watch alert confirmed!",
			"""
			<p>We are pleased to confirm your watch alert for classroom %s on the %s from %s to %s. If the booking is cancelled you will be notified.</p>
			"""
			),

	WATCH_ALERT_TRIGGERED(
			"A slot you were watching has opened up!",
			"""
			<p>A booking for classroom %s on the %s from %s to %s has been cancelled. Hurry up and book it before someone else does!.</p>
			"""
			)		
	;

	private final String subject;
	private final String messageText;

	private NotificationType(String subject, String messageText) {
		this.subject = subject;
		this.messageText = messageText;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessageText() {
		return messageText;
	}

}