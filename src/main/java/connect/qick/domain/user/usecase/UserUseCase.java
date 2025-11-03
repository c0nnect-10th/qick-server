package connect.qick.domain.user.usecase;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public void checkTeacherCode(String teacherCode) {
        if (userRepository.existsByTeacherCode(teacherCode))
            throw new AuthException(AuthStatusCode.INVALID_TEACHER_CODE);
    }

    public void saveTeacher(UserEntity user) {
        userRepository.save(user);
    }

}
