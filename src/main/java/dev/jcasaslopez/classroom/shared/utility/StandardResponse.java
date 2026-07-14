package dev.jcasaslopez.classroom.shared.utility;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

//StandardResponse is a common structure for HTTP responses (both successful responses and errors) in the application, 
//ensuring a unified response format throughout the API.

//	   return new ResponseEntity<>(new StandardResponse(
//	       LocalDateTime.now(),
//	       "Resource created successfully",
//	       "User ID: 123",
//	       HttpStatus.CREATED), 
//      HttpStatus.CREATED);

public record StandardResponse <T>(
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
		LocalDateTime timestamp,		
		String message,
		T details,
		HttpStatus status) {

	public StandardResponse(String message, T details, HttpStatus status) {
		this(LocalDateTime.now(), message, details, status);
	}

}