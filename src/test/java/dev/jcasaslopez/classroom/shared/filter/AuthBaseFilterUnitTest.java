package dev.jcasaslopez.classroom.shared.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.jcasaslopez.classroom.shared.domain.AuthResponse;
import dev.jcasaslopez.classroom.shared.domain.UserInfo;
import dev.jcasaslopez.classroom.shared.enums.AuthStatus;
import dev.jcasaslopez.classroom.shared.security.JwtService;
import dev.jcasaslopez.classroom.shared.utility.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthBaseFilterUnitTest {

	@Mock JwtService jwtService;
	@Mock FilterChain filterChain;
	@Mock HttpServletRequest request;
	@Mock HttpServletResponse response;

	private static final String EMAIL = "user@example.com";
	private static final Integer USER_ID = 1;
    private final static String secretKey = "MTIzNDU2Nzg5MEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaMDEyMzQ1Njc4OTA=";

	private TestAuthenticationFilter filter;

	@BeforeEach
	void setUp() {
        filter = new TestAuthenticationFilter(jwtService, secretKey);
	}

	@Test
	void auth_filter_returns_error_401_if_jwt_is_invalid() throws ServletException, IOException {
		// Arrange
		// Directly set the field instead of mocking JwtService: we are testing the base filter own logic, 
		// not how tokens get validated.
		filter.tokenValidationResult = new AuthResponse(AuthStatus.UNAUTHORIZED);

		// Act
		filter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(response).sendError(401, "Authentication failed");
		verify(filterChain, never()).doFilter(any(), any());
	}
	
	@Test
	void auth_filter_returns_error_403_if_role_is_insufficient() throws ServletException, IOException {
	    // Arrange
	    filter.tokenValidationResult = new AuthResponse(AuthStatus.FORBIDDEN);

	    // Act
	    filter.doFilterInternal(request, response, filterChain);

	    // Assert
	    verify(response).sendError(403, "Forbidden");
	    verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void auth_filter_continues_with_the_filter_chain_when_jwt_is_valid() throws ServletException, IOException {
		// Arrange
		filter.tokenValidationResult = new AuthResponse(AuthStatus.AUTHENTICATED, new UserInfo(EMAIL, USER_ID));

		// Act
		filter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void auth_filter_sets_user_info_correctly_in_ThreadLocal() throws IOException, ServletException {
		// Arrange
		filter.tokenValidationResult = new AuthResponse(AuthStatus.AUTHENTICATED, new UserInfo(EMAIL, USER_ID));

		// UserContext gets cleared in the "finally" block right after filterChain.doFilter() runs, so by the time 
		// doFilterInternal() returns, the values are already gone.
		//
		// By default, mocking a void method makes Mockito do nothing when it's called. doAnswer() replaces that 
		// "do nothing" with our own code, which runs exactly when the production code invokes 
		// filterChain.doFilter(request, response). "invocation" represents that actual call (its args, target method...) 
		// unused here, but available if needed. The lambda body IS what gets executed in place of the real doFilter() call.
		doAnswer(invocation -> {
			assertAll("UserContext values",
					() -> assertEquals(EMAIL, UserContext.getEmail(), "User email should match"),
					() -> assertEquals(USER_ID, UserContext.getIdUser(), "User ID should match")
					);
			return null;
		}).when(filterChain).doFilter(any(), any());

		// Act
		filter.doFilterInternal(request, response, filterChain);
	}

	@Test
	void auth_filter_clears_UserContext_after_successful_request() throws ServletException, IOException {
		// Arrange
		filter.tokenValidationResult = new AuthResponse(AuthStatus.AUTHENTICATED, new UserInfo(EMAIL, USER_ID));

		// Act
		filter.doFilterInternal(request, response, filterChain);

		// Assert
		assertAll("UserContext cleanup",
				() -> assertThrows(IllegalStateException.class, () -> UserContext.getEmail()),
				() -> assertThrows(IllegalStateException.class, () -> UserContext.getIdUser())
				);
	}
	
	@Test
	void auth_filter_clears_UserContext_when_filterChain_throws() throws ServletException, IOException {
	    filter.tokenValidationResult = new AuthResponse(AuthStatus.AUTHENTICATED, new UserInfo(EMAIL, USER_ID));
	    doThrow(new RuntimeException("boom")).when(filterChain).doFilter(any(), any());

	    assertThrows(RuntimeException.class, () -> filter.doFilterInternal(request, response, filterChain));

	    assertAll("UserContext cleanup after exception",
	            () -> assertThrows(IllegalStateException.class, () -> UserContext.getEmail()),
	            () -> assertThrows(IllegalStateException.class, () -> UserContext.getIdUser())
	            );
	}

	private static class TestAuthenticationFilter extends AuthenticationFilterBase {
		
		AuthResponse tokenValidationResult;

		TestAuthenticationFilter(JwtService jwtService, String secretKey) {
			super(jwtService, secretKey);
		}

		@Override
		protected AuthResponse validateToken(String authHeader) {
			return tokenValidationResult;
		}

		@Override
		protected boolean shouldNotFilter(HttpServletRequest request) {
			return false;
		}
	}
}