package dev.jcasaslopez.classroom.shared.dto;

import dev.jcasaslopez.classroom.shared.enums.AuthStatus;

public record AuthResponse(AuthStatus authStatus, UserInfo userInfo) {

	// Overloaded constructor for failure authentication scenarios (no user info)
    public AuthResponse(AuthStatus authStatus) {
        this(authStatus, null);
    }

	
}
