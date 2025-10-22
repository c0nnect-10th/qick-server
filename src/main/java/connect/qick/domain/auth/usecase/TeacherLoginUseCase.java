package connect.qick.domain.auth.usecase;

import connect.qick.domain.auth.dto.request.TeacherLoginRequest;
import connect.qick.domain.auth.dto.request.TeacherSignUpRequest;
import connect.qick.domain.auth.dto.response.TeacherLoginResponse;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.user.entity.Teacher;
import connect.qick.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static connect.qick.domain.auth.exception.AuthStatusCode.INVALID_CREDENTIALS;

@Service
@RequiredArgsConstructor
public class TeacherLoginUseCase {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TeacherLoginResponse login(TeacherLoginRequest request) {
        if (false) { // 일치하지 않을 때
            throw new AuthException(INVALID_CREDENTIALS);
        }

//        Teacher teacher = repo..;

        String accessToken = jwtProvider.generateAccessToken();
        String refreshToken = jwtProvider.generateRefreshToken();
        return new TeacherLoginResponse();
    }

}
