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
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
    }

    public void checkStudentEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
    }

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

}
