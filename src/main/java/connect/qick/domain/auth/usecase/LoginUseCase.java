package connect.qick.domain.auth.usecase;

import connect.qick.domain.auth.dto.request.SignInRequest;
import connect.qick.domain.auth.dto.response.SignInResponse;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserRole;
import connect.qick.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final JwtProvider jwtProvider;

    public SignInResponse login(SignInRequest request) {
        // 레포에서 확인하는 코드

        UserEntity user = new UserEntity();
        String email = "";
        UserRole role = UserRole.STUDENT;

        String accessToken = jwtProvider.generateAccessToken(email, role);
        String refreshToken = jwtProvider.generateRefreshToken(email, role);
        return new SignInResponse(user, accessToken, refreshToken);
    }

}
