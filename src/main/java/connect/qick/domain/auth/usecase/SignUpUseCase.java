package connect.qick.domain.auth.usecase;

import connect.qick.domain.auth.dto.request.TeacherSignUpRequest;
import connect.qick.domain.auth.dto.request.StudentSignUpRequest;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {

    private final UserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;

    // Teacher 회원가입
    public void signup(TeacherSignUpRequest request) {
        userUseCase.checkTeacherCode(request.teacherCode());
        UserEntity user = UserEntity.builder()
                .userType(UserType.TEACHER)
                .name(request.username())
                .teacherCode(passwordEncoder.encode(request.teacherCode()))
                .build();
        userUseCase.saveUser(user);
    }

    // Student 회원가입
    public void signup(StudentSignUpRequest request) {
        userUseCase.checkStudentEmail(request.email());
        UserEntity user = UserEntity.builder()
                .userType(UserType.STUDENT)
                .googleId(request.googleId())
                .email(request.email())
                .grade(request.grade())
                .classNumber(request.classNumber())
                .number(request.number())
                .name(request.username())
                .build();
        userUseCase.saveUser(user);
    }

}
