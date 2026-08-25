package dev.jcasaslopez.classroom.shared.security;

import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jcasaslopez.classroom.shared.domain.AuthResponse;
import dev.jcasaslopez.classroom.shared.domain.UserInfo;
import dev.jcasaslopez.classroom.shared.enums.AuthStatus;
import dev.jcasaslopez.classroom.shared.enums.RoleName;
import dev.jcasaslopez.classroom.shared.enums.TokenType;
import dev.jcasaslopez.classroom.shared.exception.UnauthorizedRoleException;
import dev.jcasaslopez.classroom.shared.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public final class JwtService {

	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	// validateJwt() is overloaded: HEADER, KEY and TOKEN TYPE are always necessary, whereas USER ROLES 
	// are only to access Classroom micro-service (only admins are allowed), hence the overloaded validateJwt().

	public AuthResponse validateJwt(String header, String base64SecretKey, TokenType tokenTypeValid) {
		try {
			Claims claims = parseAndValidateToken(header, base64SecretKey, tokenTypeValid);
			
			logger.info("Token validated for user: {}", claims.get("email", String.class));
			UserInfo userInfo = new UserInfo(claims.get("email", String.class), claims.get("idUser", Integer.class));
			return new AuthResponse(AuthStatus.AUTHENTICATED, userInfo); 
			
		} catch (InvalidTokenException ex) {
			logger.warn("Validation failed: {}", ex.getMessage());
			return new AuthResponse(AuthStatus.UNAUTHORIZED);
		}
	}

	@SuppressWarnings("unchecked")
	public AuthResponse validateJwt(String header, String base64SecretKey, TokenType tokenTypeValid, List<RoleName> validRoles) {
		try {
			Claims claims = parseAndValidateToken(header, base64SecretKey, tokenTypeValid);
			
			hasAnyValidRole(validRoles, claims.get("roles", List.class));
			
			logger.info("Token validated");
			UserInfo userInfo = new UserInfo(claims.get("email", String.class), claims.get("idUser", Integer.class));
			return new AuthResponse(AuthStatus.AUTHENTICATED, userInfo); 
			
		} catch (InvalidTokenException ex) {
			logger.warn("Validation failed: {}", ex.getMessage());
			return new AuthResponse(AuthStatus.UNAUTHORIZED);
			
		} catch (UnauthorizedRoleException ex) {
			logger.warn("Access denied: {}", ex.getMessage());
			return new AuthResponse(AuthStatus.FORBIDDEN);
		}
	}

	private Claims parseAndValidateToken(String header, String base64SecretKey, TokenType tokenTypeValid) {
		SecretKey key = decodeBase64SecretKey(base64SecretKey);
		String token = extractJwt(header);
		Claims claims = parseJwt(token, key);
		tokenTypeIsValid(tokenTypeValid, claims.get("purpose", String.class));
		return claims;
	}

	private SecretKey decodeBase64SecretKey(String base64SecretKey) {
		// Convert the Base64 encoded string back into a raw byte array.
		byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);

		// Create a secure HMAC signing key from the bytes and validate its length.
		return Keys.hmacShaKeyFor(keyBytes);			
	}

	private String extractJwt(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new InvalidTokenException("Token is missing or does not start with 'Bearer '");
		}
		return authHeader.substring(7);
	}

	private Claims parseJwt(String token, SecretKey key) {
		try {
			Claims claims = Jwts.parser()

					// Sets the key that will be used to verify the signature.
					.verifyWith(key)

					// Builds the JWT parser with the specified configuration.
					.build()

					// This is where all verifications happen.
					.parseSignedClaims(token)
					.getPayload();
			return claims;
		} catch (ExpiredJwtException | MalformedJwtException | io.jsonwebtoken.security.SecurityException ex) {
			throw new InvalidTokenException("Expired or malformed token");
		}
	}

	private void tokenTypeIsValid(TokenType tokenTypeValid, String tokenTypeFoundInJwt) {
	    if (tokenTypeFoundInJwt == null || !tokenTypeValid.name().equalsIgnoreCase(tokenTypeFoundInJwt)) {
	        throw new InvalidTokenException(String.format("Invalid token type. Expected: %s, Found: %s", tokenTypeValid.name(), tokenTypeFoundInJwt));
	    }
	}

	private void hasAnyValidRole(List<RoleName> validRoles, List<String> rolesFoundInJwt) {
		boolean hasValidRole = validRoles.stream()
				.anyMatch(role -> rolesFoundInJwt.contains(role.name()));
		if (!hasValidRole) {
			throw new UnauthorizedRoleException(String.format("Access denied. User roles %s do not match any allowed roles: %s", rolesFoundInJwt, validRoles));	
		} 
	}
}