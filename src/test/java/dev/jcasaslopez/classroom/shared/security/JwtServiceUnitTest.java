package dev.jcasaslopez.classroom.shared.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.jcasaslopez.classroom.shared.domain.AuthResponse;
import dev.jcasaslopez.classroom.shared.enums.AuthStatus;
import dev.jcasaslopez.classroom.shared.enums.RoleName;
import dev.jcasaslopez.classroom.shared.enums.TokenType;

public class JwtServiceUnitTest {
	
	private JwtService jwtService;
	
	private static final String VALID_SECRET_KEY = "MTIzNDU2Nzg5MEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaMDEyMzQ1Njc4OTA=";
	private static final String VALID_TOKEN = new GenerateJwt(VALID_SECRET_KEY).build();
	private static final String SUPER_SHORT_TOKEN = new GenerateJwt(VALID_SECRET_KEY).withExpirationInMs(1).build();
	private static final String MALFORMED_TOKEN = "Malformed token";
	private static final String VALID_TOKEN_WITH_ADMIN_ROLE = new GenerateJwt(VALID_SECRET_KEY).withRoleAdmin().build();
	
	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
	}
	
	@Test
	void happy_path_for_validateJwt_no_roles() {
		// Arrange

		// Act
		AuthResponse authResponse = jwtService.validateJwt(addBearer(VALID_TOKEN), VALID_SECRET_KEY, TokenType.ACCESS);
		
		// Assert
		assertAll(
				() -> assertEquals(AuthStatus.AUTHENTICATED, authResponse.authStatus()),
				() -> assertNotNull(authResponse.userInfo())
				);	
	}
	
	@Test
	void happy_path_for_validateJwt_with_roles() {
		// Arrange

		// Act
		AuthResponse authResponse = jwtService.validateJwt(addBearer(VALID_TOKEN_WITH_ADMIN_ROLE), VALID_SECRET_KEY, 
				TokenType.ACCESS, List.of(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));

		// Assert
		assertAll(
				() -> assertEquals(AuthStatus.AUTHENTICATED, authResponse.authStatus()),
				() -> assertNotNull(authResponse.userInfo())
				);		
	}
	
	@Test
	void when_token_has_insufficient_roles_JwtService_returns_FORBIDDEN() {
		// Arrange

		// Act
		AuthResponse authResponse = jwtService.validateJwt(addBearer(VALID_TOKEN), VALID_SECRET_KEY, TokenType.ACCESS,
				List.of(RoleName.ROLE_ADMIN));

		// Assert
		assertAll(
				() -> assertEquals(AuthStatus.FORBIDDEN, authResponse.authStatus()),
				() -> assertNull(authResponse.userInfo())
				);		
	}

	@ParameterizedTest
	@MethodSource("jwtValidateUnauthorizedData")
	void when_token_invalid_JwtService_returns_UNAUTHORIZED(String Bearer, String secretKey, TokenType tokenType) {
		// Arrange
		
		// Act
		AuthResponse authResponse = jwtService.validateJwt(Bearer, secretKey, tokenType);
		
		// Assert
		assertAll(
				() -> assertEquals(AuthStatus.UNAUTHORIZED, authResponse.authStatus()),
				() -> assertNull(authResponse.userInfo())
				);		
	}
	
	private static Stream<Arguments> jwtValidateUnauthorizedData() {
		return Stream.of(
				// Header is null
				Arguments.of(null, VALID_SECRET_KEY, TokenType.ACCESS),

				// Header misses "Bearer..."
				Arguments.of(VALID_TOKEN, VALID_SECRET_KEY, TokenType.ACCESS),

				// Expired token (token life of only 1ms)
				Arguments.of(addBearer(SUPER_SHORT_TOKEN), VALID_SECRET_KEY, TokenType.ACCESS),

				// Malformed token
				Arguments.of(addBearer(MALFORMED_TOKEN), VALID_SECRET_KEY, TokenType.ACCESS),

				// Incorrect token type, token is formed by default with TokenType.ACCESS (see JwtService)
				Arguments.of(addBearer(VALID_TOKEN), VALID_SECRET_KEY, TokenType.VERIFICATION)
				);

	}
	
	private static String addBearer(String token) {
		return token != null ? "Bearer " + token : null;
	}

}
