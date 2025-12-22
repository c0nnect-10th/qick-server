package connect.qick.domain.user.service;

import connect.qick.domain.user.dto.request.UpdateUserRequest;
import connect.qick.domain.user.entity.UserEntity;
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

    public UserEntity getUserByUserId(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
    }

    @Transactional
    public void updateUser(String googleId, UpdateUserRequest request) {
        UserEntity user = getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        user.updateUserProfile(request);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

}
