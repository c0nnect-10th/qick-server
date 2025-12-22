package connect.qick.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.auth.dto.response.UserResolveResult;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.service.UserService;
import connect.qick.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GoogleIdTokenVerifier idTokenVerifier;
    private final UserService userService;

    @Transactional
    public LoginResponse login(final String idToken) {
        GoogleIdToken googleIdToken = verifyIdToken(idToken);
        UserResolveResult resolveResult = getOrCreateUser(googleIdToken);
        String accessToken = jwtProvider.generateAccessToken(resolveResult.getUser().getId(), resolveResult.getUser().getUserType());
        String refreshToken = jwtProvider.generateRefreshToken(resolveResult.getUser().getId(), resolveResult.getUser().getUserType());
        return new LoginResponse(accessToken, refreshToken, resolveResult.getIsNewUser());
    }


    public GoogleIdToken verifyIdToken(final String idToken) {
        try {
            GoogleIdToken googleIdToken = idTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new GeneralSecurityException("Invalid idToken");
            }

            return googleIdToken;
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String refresh(String refreshToken) {
        Jws<Claims> claims = jwtProvider.getClaims(refreshToken);
        return jwtProvider.refresh(claims.getPayload());
    }


    private UserResolveResult getOrCreateUser(GoogleIdToken token) {
        String googleId = token.getPayload().getSubject();
        Optional<UserEntity> opt = userService.getUserByGoogleId(googleId);
        if (opt.isPresent()) {
            return new UserResolveResult(opt.get());
        }

        return new UserResolveResult(true, userService.saveUser(
            UserEntity.builder()
                .userType(UserType.GUEST)
                .googleId(googleId)
                .build()));
        }
}
