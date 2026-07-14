package dev.jcasaslopez.classroom.shared.event;

public record NotificationEvent (String subject, String message, String emailAddress, String log){}