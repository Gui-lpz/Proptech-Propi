package cr.ac.ucr.paraiso.propi.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationMs;

    public String generateToken(
            String username,
            List<String> roles,
            String sessionId) {

        Date issuedAt = new Date();
        Date expiration = new Date(
                issuedAt.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("sessionId", sessionId)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    public String getSessionId(String token) {
        return getClaims(token).get("sessionId", String.class);
    }

    public long getExpirationTime(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
