package dev.jcasaslopez.classroom.shared.security;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jcasaslopez.classroom.shared.enums.RoleName;
import dev.jcasaslopez.classroom.shared.enums.TokenType;
import dev.jcasaslopez.classroom.shared.exception.FailedAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public final class JwtService {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	// validateJwt() is overloaded: HEADER, KEY and TOKEN TYPE are always necessary, whereas USER ROLES 
	// are only to access Classroom micro-service (only admins are allowed), hence the overloaded validateJwt().
	
	// It returns an Optional with the user's email if the authentication has been successful or an empty one if it has not.
	public Optional<String> validateJwt (String header, String base64SecretKey, TokenType tokenTypeValid){
		try {
			SecretKey key = decodeBase64SecretKey(base64SecretKey);
			String token = extractJwt(header);
			Claims claims = parseJwt(token, key);
			tokenTypeIsValid(tokenTypeValid, claims.get("purpose", String.class));
			logger.info("Token validated for user: {}", claims.get("email", String.class));
			return Optional.of(claims.get("email", String.class));
		} catch (FailedAuthenticationException ex) {
			logger.warn("Validation failed: {}", ex.getMessage());
			return Optional.empty();
		}
	}

	// It returns an Optional with the user's email if the authentication has been successful or an empty one if it has not.
	@SuppressWarnings("unchecked")
	public Optional<String> validateJwt (String header, String base64SecretKey, TokenType tokenTypeValid, List<RoleName> validRoles) {
		try {
			SecretKey key = decodeBase64SecretKey(base64SecretKey);
			String token = extractJwt(header);
			Claims claims = parseJwt(token, key);
			tokenTypeIsValid(tokenTypeValid, claims.get("purpose", String.class));
			hasAnyValidRole(validRoles, claims.get("roles", List.class));
			logger.info("Token validated");
			return Optional.of(claims.get("email", String.class));
		} catch (FailedAuthenticationException ex) {
			logger.warn("Validation failed: {}", ex.getMessage());
			return Optional.empty();
		}
	}
	
	public Integer extractIdUser(String header, String base64SecretKey) {
	    try {
	        SecretKey key = decodeBase64SecretKey(base64SecretKey);
	        String token = extractJwt(header);
	        Claims claims = parseJwt(token, key);
	    	return claims.get("idUser", Integer.class);
	    } catch (FailedAuthenticationException ex) {
	        logger.warn("Failed to extract idUser from token: {}", ex.getMessage());
	        return null;
	    }
	}

	private SecretKey decodeBase64SecretKey(String base64SecretKey) {
		// Convert the Base64 encoded string back into a raw byte array.
		byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);

		// Create a secure HMAC signing key from the bytes and validate its length.
		return Keys.hmacShaKeyFor(keyBytes);			
	}

	private String extractJwt(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new FailedAuthenticationException("Token is missing or does not start with 'Bearer '");
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
			throw new FailedAuthenticationException("Expired or malformed token");
		}
	}
	
	private void tokenTypeIsValid(TokenType tokenTypeValid, String tokenTypeFoundInJwt) {
		if(!tokenTypeValid.prefix().equals(tokenTypeFoundInJwt)) {
			throw new FailedAuthenticationException (String.format("Invalid token type. Expected: %s, Found: %s", tokenTypeValid.prefix(), tokenTypeFoundInJwt));
		}
	}

	private void hasAnyValidRole(List<RoleName> validRoles, List<String> rolesFoundInJwt) {
		boolean hasValidRole = validRoles.stream()
				.anyMatch(role -> rolesFoundInJwt.contains(role.name()));
		if (!hasValidRole) {
	        throw new FailedAuthenticationException(String.format("Access denied. User roles %s do not match any allowed roles: %s", rolesFoundInJwt, validRoles));	
		} 
	}
}