package connect.qick.domain.auth.usecase;

import connect.qick.domain.auth.dto.request.TeacherSignUpRequest;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.Teacher;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserRole;
import org.springframework.stereotype.Service;

@Service
public class TeacherSignUpUseCase {

    public void signup(TeacherSignUpRequest request) {
        if (false) { // t_code가 존재하지 않는 경우
            throw new AuthException(AuthStatusCode.INVALID_T_CODE_EXCEPTION);
        }
        if (false) { // 이미 존재하는 teacher인 경우
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS_EXCEPTION);
        }

        UserEntity user = UserEntity.builder()
                .name(request.name())
                .role(UserRole.TEACHER)
                .build();
//        repo.save(user)
        Teacher teacher = Teacher.builder()
                .name(request.name())
                .tCode(request.tCode())
                .user(user)
                .build();
//        repo.save(teacher);
    }
}
