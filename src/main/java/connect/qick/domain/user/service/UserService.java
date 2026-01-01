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

import java.util.List;
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

    public UserEntity getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
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
    public UserResponse updateStudent(String googleId, UpdateStudentRequest request) {
        UserEntity user = getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        user.updateUserProfile(request);
        return UserResponse.from(user);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String googleId) {
        if (!userRepository.existsByGoogleId(googleId)) {
            throw new UserException(UserStatusCode.NOT_FOUND);
        }
        userRepository.deleteByGoogleId(googleId);
    }

    @Transactional
    public void updateFcmToken(String googleId, String fcmToken) {
        UserEntity user = getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    public List<UserEntity> getUsersByUserType(UserType userType) {
        return userRepository.findAllByUserType(userType);
    }
}
