package dev.jcasaslopez.classroom.shared.security;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import dev.jcasaslopez.classroom.shared.enums.RoleName;
import dev.jcasaslopez.classroom.shared.enums.TokenType;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.Jwts;

public class GenerateJwt {
	
    private String tokenType = "JWT";
    private String username = "Username";
    private final String idToken = UUID.randomUUID().toString();
    private List<String> roles = new ArrayList<>(List.of(String.valueOf(RoleName.ROLE_USER)));
    private int idUser = 1;
    private String tokenPurpose = String.valueOf(TokenType.ACCESS);
    private String email = "user@example.com";
    private long expirationTimeMs = 3_600_000; 
    private final SecretKey key;

    public GenerateJwt() {
        this.key = createSecretKey();
    }

    private SecretKey createSecretKey() {
        String base64SecretKey = "MTIzNDU2Nzg5MEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaMDEyMzQ1Njc4OTA=";
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public GenerateJwt withTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public GenerateJwt withUsername(String username) {
        this.username = username;
        return this;
    }

    public GenerateJwt withRoleAdmin() {
        this.roles.add(String.valueOf(RoleName.ROLE_ADMIN));
        return this;
    }

    public GenerateJwt withRoleSuperAdmin() {
        this.roles.add(String.valueOf(RoleName.ROLE_SUPERADMIN));
        return this;
    }

    public GenerateJwt withIdUser(int idUser) {
        this.idUser = idUser;
        return this;
    }

    public GenerateJwt withTokenPurpose(TokenType tokenPurpose) {
        this.tokenPurpose = String.valueOf(tokenPurpose);
        return this;
    }

    public GenerateJwt withEmail(String email) {
        this.email = email;
        return this;
    }

    public GenerateJwt withExpirationInMs(long ms) {
        this.expirationTimeMs = ms;
        return this;
    }

    public String build() {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + expirationTimeMs);

        return Jwts.builder()
                .header().type(tokenType).and()
                .subject(username)
                .id(idToken)
                .claim("roles", roles)
                .claim("idUser", idUser)
                .claim("purpose", tokenPurpose)
                .claim("email", email)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}