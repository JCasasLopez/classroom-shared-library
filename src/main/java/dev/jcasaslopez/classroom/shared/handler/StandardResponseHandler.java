package dev.jcasaslopez.classroom.shared.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.jcasaslopez.classroom.shared.dto.StandardResponse;
import jakarta.servlet.http.HttpServletResponse;

// StandardResponseHandler converts a StandardResponse into a standard HTTP response.
// It is necessary in classes like AuthenticationFilter, where StandardResponse cannot be used 
// directly, as exceptions thrown in a filter are not handled by GlobalExceptionHandler.

@Component
public class StandardResponseHandler {

	ObjectMapper objectMapper;

	public StandardResponseHandler() {
		this.objectMapper = new ObjectMapper();
		
	    // Add support for Java 8 date/time classes (LocalDate, LocalDateTime, etc.)
		objectMapper.registerModule(new JavaTimeModule());
		
	    // Write dates as ISO-8601 strings instead of numeric timestamps
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	public <T> HttpServletResponse handleResponse(HttpServletResponse response, HttpStatus status, String message, T details) throws IOException {		
	    // Configure response headers
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status.value());
		
	    // Create a StandardResponse with timestamp, message, details and status
		StandardResponse<T> standardResponse = new StandardResponse<>(message, details, status);
		
	    // Serialize StandardResponse to JSON
		String jsonResponse = objectMapper.writeValueAsString(standardResponse);
		
	    // Write JSON into the HttpServletResponse
		response.getWriter().write(jsonResponse);
		return response;
	}

}