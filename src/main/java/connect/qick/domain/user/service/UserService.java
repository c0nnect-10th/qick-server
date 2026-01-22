package connect.qick.domain.user.service;


import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.dto.response.UserRankingResponse;
import connect.qick.domain.user.dto.response.UserResponse;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean checkGoogleId(String googleId);

    UserEntity getUserByGoogleId(String googleId);

    Optional<UserEntity> getUser(String googleId);

    UserResponse getUserInfo(String googleId);

    UserEntity getUserByUserId(Long userId);

    @Transactional
    void signupStudent(String googleId, SignupStudentRequest request);

    @Transactional
    UserResponse updateStudent(String googleId, UpdateStudentRequest request);

    UserEntity saveUser(UserEntity user);

    @Transactional
    void deleteUser(String googleId);

    @Transactional
    void updateFcmToken(String googleId, String fcmToken);

    boolean isValidFcmToken(String fcmToken);

    List<UserEntity> getUsersByUserType(UserType userType);

    List<UserRankingResponse> getTopUsersByPoints(int limit);
}
