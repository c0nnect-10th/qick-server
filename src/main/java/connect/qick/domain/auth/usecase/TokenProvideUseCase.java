package connect.qick.domain.auth.usecase;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.global.security.jwt.JwtExtract;
import connect.qick.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenProvideUseCase {

    private final JwtProvider jwtProvider;
    private final JwtExtract jwtExtract;

    public String getAccessToken(UserEntity user) {
        return jwtProvider.generateAccessToken(user.getId(), user.getUserType());
    }

    public String getRefreshToken(UserEntity user) {
        return jwtProvider.generateRefreshToken(user.getId(), user.getUserType());
    }

    public String refreshToken(String token) {
        return jwtProvider.reprovideToken(jwtProvider.getClaims(token).getPayload());
    }

}
