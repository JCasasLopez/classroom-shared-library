package dev.jcasaslopez.classroom.shared.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import dev.jcasaslopez.classroom.shared.enums.RoleName;
import dev.jcasaslopez.classroom.shared.enums.TokenPurpose;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class GenerateJwt {
	
    private List<String> roles = new ArrayList<>(List.of(String.valueOf(RoleName.ROLE_USER)));
    private int idUser = 1;
    private String email = "user@example.com";
    private long expirationTimeMs = TokenPurpose.ACCESS.timeToLive().toMillis();
    private final SecretKey key;

    public GenerateJwt(String base64SecretKey) {
        this.key = createSecretKey(base64SecretKey);
    }

    private SecretKey createSecretKey(String base64SecretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
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

    public GenerateJwt withEmail(String email) {
        this.email = email;
        return this;
    }

    public GenerateJwt withExpirationInMs(long ms) {
        this.expirationTimeMs = ms;
        return this;
    }

    public String build() {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(idUser))
                .claim("roles", roles)
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationTimeMs)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}