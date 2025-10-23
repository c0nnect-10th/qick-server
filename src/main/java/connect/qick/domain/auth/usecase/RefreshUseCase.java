package connect.qick.domain.auth.usecase;

import connect.qick.domain.auth.dto.request.RefreshTokenRequest;
import connect.qick.domain.auth.dto.response.RefreshTokenResponse;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.global.security.jwt.JwtExtract;
import connect.qick.global.security.jwt.JwtProvider;
import connect.qick.global.security.jwt.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshUseCase {

    private final JwtExtract jwtExtract;
    private final JwtProvider jwtProvider;

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        final Jws<Claims> jws = jwtProvider.getClaims(
                jwtExtract.extractToken(request.refreshToken())
        );
        final Claims claims = jws.getPayload();
        if (!jwtExtract.isCorrect(claims, TokenType.REFRESH)) {
            throw new AuthException(AuthStatusCode.NOT_REFRESH_TOKEN);
        }

        //TODO: refreshToken rotation 필요
        return RefreshTokenResponse.builder()
                .accessToken(jwtProvider.reprovideToken(claims))
                .build();
    };
}
