package connect.qick.global.security.jwt;


import connect.qick.domain.user.enums.UserRole;
import connect.qick.global.security.jwt.config.JwtProperties;
import connect.qick.global.security.jwt.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private final SecretKey key;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parser(). // parser 수정
                    verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        }
        catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Expired token", e);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    public String generateToken(TokenType tokenType, String email, UserRole role, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(email)
                .claim("token_type", tokenType.name())
                .claim("authority", role.getKey())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration, ChronoUnit.MILLIS)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateAccessToken(String email, UserRole role) {
        return generateToken(TokenType.ACCESS, email, role, jwtProperties.getAccessExpiration());
    }

    public String generateRefreshToken(String email, UserRole role) {
        return generateToken(TokenType.REFRESH, email, role, jwtProperties.getRefreshExpiration());
    }

}
