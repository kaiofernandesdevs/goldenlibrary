package br.com.goldenlibrary.goldenlibrary_api.security;

import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            return Jwts.builder()
                    .issuer("goldenlibrary-api")
                    .subject(user.getEmail())
                    .expiration(Date.from(generatedExpInstantDate()))
                    .signWith(getSecretKey())
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    public String validatedToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Instant generatedExpInstantDate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}