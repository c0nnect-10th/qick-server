package connect.qick.domain.user.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.dto.response.UserResponse;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean checkGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }

    public Optional<UserEntity> getUserByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public UserResponse getUserInfo(String googleId) {
        return UserResponse.from(
                getUserByGoogleId(googleId)
                    .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND))
        );
    }

    public Optional<UserEntity> getUserByUserId(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public void signupStudent(String googleId, SignupStudentRequest request) {
        UserEntity user = getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        if(user.getUserStatus() == UserStatus.ACTIVE ) {
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
        }
        user.updateUserProfile(request);
        user.setUserType(UserType.STUDENT);
        user.setUserStatus(UserStatus.ACTIVE);
        //TODO: blacklist 추가
    }

    @Transactional
    public void updateStudent(String googleId, UpdateStudentRequest request) {
        UserEntity user = getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        user.updateUserProfile(request);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String googleId) {
        if (userRepository.existsByGoogleId(googleId)) {
            throw new UserException(UserStatusCode.NOT_FOUND);
        }
        userRepository.deleteByGoogleId(googleId);
    }
}
