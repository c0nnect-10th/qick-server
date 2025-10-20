package connect.qick.global.security.jwt;

import ch.qos.logback.core.util.StringUtil;
import connect.qick.global.security.jwt.config.JwtProperties;
import connect.qick.global.security.jwt.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtExtract {

//    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;

    public String extractTokenFromRequest(HttpServletRequest request) {
        return extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    public Authentication getAuthentication(final String token) {
        final Jws<Claims> jws = jwtProvider.getClaims(token);
        final Claims claims = jws.getPayload();
        if (!isCorrect(claims, TokenType.ACCESS)) {
            // tokenTypeException
        }

//        return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities())

    }

    public String extractToken(final String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return token;
    }


    public boolean isCorrect(final Claims claims, final TokenType tokenType) {
        return claims.get("token_type").equals(tokenType.toString());
    }

}
