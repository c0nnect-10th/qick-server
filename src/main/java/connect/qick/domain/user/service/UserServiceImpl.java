package connect.qick.domain.user.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.SignupTeacherRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.dto.response.SignupResponse;
import connect.qick.domain.user.dto.response.UserRankingResponse;
import connect.qick.domain.user.dto.response.UserResponse;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.tcode.service.TCodeService;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final TCodeService tCodeService;
    private final VolunteerApplicationRepository volunteerApplicationRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;

    @Override
    public boolean checkGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }

    @Override
    public UserEntity getUserByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
            .orElseThrow(() -> new AuthException(AuthStatusCode.UNAUTHORIZED));
    }

    @Override
    public Optional<UserEntity> getUser(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    @Override
    public UserResponse getUserInfo(String googleId) {
        return UserResponse.from(
                getUserByGoogleId(googleId)
        );
    }

    @Override
    public UserEntity getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
    }

    @Transactional
    @Override
    public SignupResponse signupStudent(String googleId, SignupStudentRequest request) {
        UserEntity user = getUserByGoogleId(googleId);
        if(user.getUserStatus() == UserStatus.ACTIVE) {
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
        }

        user.signupStudent(request);
        String access = jwtProvider.generateAccessToken(googleId, UserType.STUDENT);
        String refresh = jwtProvider.generateRefreshToken(googleId, UserType.STUDENT);
        return new SignupResponse(access, refresh);
    }

    @Transactional
    @Override
    public SignupResponse signupTeacher(String googleId, SignupTeacherRequest request) {
        UserEntity user = getUserByGoogleId(googleId);
        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new AuthException(AuthStatusCode.ALREADY_EXISTS);
        }

        String teacherName = request.name().trim();
        String teacherCode = request.teacherCode().trim();

        // 코드를 비관적 락으로 조회 후 같은 트랜잭션에서 소모해 중복 가입을 방지한다.
        tCodeService.verifyTCode(teacherCode, teacherName);

        user.signupTeacher(teacherName, teacherCode);
        String access = jwtProvider.generateAccessToken(googleId, UserType.TEACHER);
        String refresh = jwtProvider.generateRefreshToken(googleId, UserType.TEACHER);
        return new SignupResponse(access, refresh);
    }

    @Transactional
    @Override
    public UserResponse updateStudent(String googleId, UpdateStudentRequest request) {
        UserEntity user = getUserByGoogleId(googleId);
        user.updateUserProfile(request);
        return UserResponse.from(user);
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String googleId) {
        UserEntity user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));

        if (user.getUserType() == UserType.STUDENT) {
            List<VolunteerApplicationEntity> appliedApplications =
                    volunteerApplicationRepository.findByStudentIdAndStatus(
                            user.getId(),
                            ApplicationStatus.APPLIED
                    );
            for (VolunteerApplicationEntity application : appliedApplications) {
                application.cancelByStudentWithdrawal("회원 탈퇴로 신청이 자동 취소되었습니다.");
            }
        }

        if (user.getUserType() == UserType.TEACHER) {
            List<VolunteerWorkEntity> teacherWorks = volunteerWorkRepository.findActiveByTeacherIdForUpdate(user.getId());
            for (VolunteerWorkEntity teacherWork : teacherWorks) {
                teacherWork.cancelByTeacherWithdrawal("담당 선생님 탈퇴로 봉사활동이 자동 취소되었습니다.");
            }
        }

        user.softDelete();
    }

    @Transactional
    @Override
    public void updateFcmToken(String googleId, String fcmToken) {
        UserEntity user = getUserByGoogleId(googleId);

        if (!isValidFcmToken(fcmToken)) {
            return;
        }

        user.setFcmToken(fcmToken.trim());
        userRepository.save(user);
    }

    @Override
    public boolean isValidFcmToken(String fcmToken) {
        if (fcmToken == null) {
            return false;
        }

        String token = fcmToken.trim();
        if (token.isEmpty()) {
            return false;
        }

        if (token.length() < 10 || token.length() > 4096) {
            return false;
        }

        return token.matches("^[A-Za-z0-9_\\-:.]+$");
    }
    @Override
    public List<UserEntity> getUsersByUserType(UserType userType) {
        return userRepository.findAllByUserType(userType);
    }
    @Override
    public List<UserRankingResponse> getTopUsersByPoints(int limit) {
        List<UserEntity> topUsers = userRepository.findByUserTypeOrderByTotalPointsDesc(
                UserType.STUDENT,
                PageRequest.of(0, limit)
        );

        return IntStream.range(0, topUsers.size())
                .mapToObj(i -> UserRankingResponse.from(topUsers.get(i), i + 1))
                .collect(Collectors.toList());
    }
}
