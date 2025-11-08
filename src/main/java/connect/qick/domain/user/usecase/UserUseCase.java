package connect.qick.domain.user.usecase;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void checkTeacherCode(String teacherCode) {
        List<UserEntity> users = userRepository.findAll();
        users.stream()
                .filter(user ->
                        passwordEncoder.matches(teacherCode, user.getTeacherCode())
                )
                .findFirst()
                .ifPresent((user) -> {
                    throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
                });

    }

    public void checkStudentEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
    }

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    public UserEntity getTeacher(String teacherCode, String name) {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .filter(user ->
                        passwordEncoder.matches(teacherCode, user.getTeacherCode()) &&
                        user.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthStatusCode.INVALID_TEACHER_CODE));
    }

}
