package dev.jcasaslopez.classroom.shared.event;

import dev.jcasaslopez.classroom.shared.enums.NotificationType;

public class NotificationEvent {
	
	private String subject;
	private String logText;
	private String messageText;
	private String emailAddress;
	
	public NotificationEvent(NotificationType notification, String emailAddress) {
		this.subject = notification.getSubject();
		this.logText = notification.getLogText();
		this.messageText = notification.getMessageText();
	}

	public NotificationEvent() {
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLogText() {
		return logText;
	}

	public void setLogText(String logText) {
		this.logText = logText;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
