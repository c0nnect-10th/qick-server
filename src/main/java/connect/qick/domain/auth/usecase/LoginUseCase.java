package connect.qick.domain.auth.usecase;

import connect.qick.domain.auth.dto.request.TeacherLoginRequest;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginUseCase {

    private final UserUseCase userUseCase;
    private final TokenProvideUseCase provideUseCase;

    public LoginResponse login(String TeacherCode, String name) {
        UserEntity user = userUseCase.getTeacher(TeacherCode, name);
        String access = provideUseCase.getAccessToken(user);
        String refresh = provideUseCase.getRefreshToken(user);

        return new LoginResponse(access, refresh);
    }

}
