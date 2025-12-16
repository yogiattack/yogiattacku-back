package com.ssafy.yogiattacku.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {
    @Value("${spring.jwt.access-token-ttl}")
    private Duration ACCESS_TOKEN_TTL;
    private static final String ROLE = "ROLE_USER";

    @Value("${spring.jwt.key}")
    private String key;
    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }


    public String generateAccessToken(Long userId) {
        Date issuedDate = new Date();
        Date expiryDate = new Date(issuedDate.getTime() + ACCESS_TOKEN_TTL.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", ROLE)
                .issuedAt(issuedDate)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired. exp={}, now={}", e.getClaims().getExpiration(), new Date());
        } catch (SecurityException | SignatureException e) {
            log.warn("JWT signature invalid");
        } catch (MalformedJwtException e) {
            log.warn("JWT malformed: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT unsupported: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT invalid: {}", e.getMessage());
        }
        return false;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
