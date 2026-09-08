package dev.jcasaslopez.classroom.shared.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.jcasaslopez.classroom.shared.domain.AuthResponse;
import dev.jcasaslopez.classroom.shared.domain.UserInfo;
import dev.jcasaslopez.classroom.shared.enums.AuthStatus;
import dev.jcasaslopez.classroom.shared.security.JwtService;
import dev.jcasaslopez.classroom.shared.utility.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AuthenticationFilterBase extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilterBase.class);
	protected final JwtService jwtService;
	protected final String base64SecretKey;

	public AuthenticationFilterBase(JwtService jwtService, String base64SecretKey) {
		this.jwtService = jwtService;
		this.base64SecretKey = base64SecretKey;
	}

	protected abstract AuthResponse validateToken(String authHeader);

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		logger.debug("Entering AuthenticationFilter...");
		String authHeader = request.getHeader("Authorization");

		AuthResponse validationResult = validateToken(authHeader);
		
		try {
			if (validationResult.authStatus() == AuthStatus.UNAUTHORIZED) {
			    response.sendError(401, "Authentication failed");
			    return;
			} else if (validationResult.authStatus() == AuthStatus.FORBIDDEN) {
			    response.sendError(403, "Forbidden");
			    return;
			} 
			
			// The user info (email and id) will be needed further on, to send notifications, search booking 
			// and watch alert history, etc, so it has to be kept at hand.
			UserInfo userInfo = validationResult.userInfo();
			UserContext.setContext(userInfo.email(), userInfo.idUser());   

			filterChain.doFilter(request, response);

		} finally {
			UserContext.clear(); 
		}
	}
}