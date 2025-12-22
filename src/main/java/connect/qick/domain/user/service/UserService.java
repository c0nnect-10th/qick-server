package connect.qick.domain.user.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.repository.UserRepository;
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

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

}
