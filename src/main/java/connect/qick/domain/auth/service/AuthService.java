package connect.qick.domain.auth.service;

import connect.qick.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;

    public String refresh(String refreshToken) {
        Jws<Claims> claims = jwtProvider.getClaims(refreshToken);
        return jwtProvider.refresh(claims.getPayload());
    }
}
