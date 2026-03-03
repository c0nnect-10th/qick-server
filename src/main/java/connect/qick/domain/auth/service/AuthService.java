package connect.qick.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.auth.dto.response.RefreshResponse;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.service.UserService;
import connect.qick.global.security.jwt.JwtExtract;
import connect.qick.global.security.jwt.JwtProvider;
import connect.qick.global.security.jwt.enums.TokenType;
import connect.qick.infra.redis.RedisRefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final static String SIGNUP_SCOPE = "user/signup/**";

    private final JwtProvider jwtProvider;
    private final JwtExtract jwtExtract;
    private final GoogleIdTokenVerifier idTokenVerifier;
    private final UserService userService;
    private final RedisRefreshTokenService redisRefreshTokenService;

    @Transactional
    public LoginResponse login(final String idToken) {
        GoogleIdToken googleIdToken = verifyIdToken(idToken);
        String googleId = googleIdToken.getPayload().getSubject();
        UserEntity user = getOrCreateUser(googleIdToken);

        if (user.getUserStatus().equals(UserStatus.ACTIVE)) {
            String accessToken = jwtProvider.generateAccessToken(googleId, user.getUserType());
            String refreshToken = jwtProvider.generateRefreshToken(googleId, user.getUserType());
            return new LoginResponse(accessToken, refreshToken, null);
        }

        return new LoginResponse(null, null,
            jwtProvider.generateSignupToken(
                googleId, List.of(SIGNUP_SCOPE)
        ));
    }


    public GoogleIdToken verifyIdToken(final String idToken) {
        try {
            GoogleIdToken googleIdToken = idTokenVerifier.verify(idToken);
            log.info(String.valueOf(googleIdToken));
            if (googleIdToken == null) {
                throw new AuthException(AuthStatusCode.INVALID_ID_TOKEN);
            }

            return googleIdToken;
        } catch (GeneralSecurityException | IOException e) {
            throw new AuthException(AuthStatusCode.INVALID_ID_TOKEN);
        }

    }


    public void logout(String refreshToken) {
        redisRefreshTokenService.removeRefreshToken(refreshToken);
    }

    public RefreshResponse refresh(String refreshToken) {
        redisRefreshTokenService.removeRefreshToken(refreshToken);

        Claims claims = jwtProvider.getClaims(refreshToken).getPayload();
        jwtExtract.checkTokenType(claims, TokenType.REFRESH);
        UserEntity user = userService.getAuthenticatedUserByGoogleId(claims.getSubject());

        String newAccess = jwtProvider.generateAccessToken(claims.getSubject(), user.getUserType());
        String newRefresh = jwtProvider.generateRefreshToken(claims.getSubject(), user.getUserType());
        return new RefreshResponse(newAccess, newRefresh);
    }


    private UserEntity getOrCreateUser(GoogleIdToken token) {
        String googleId = token.getPayload().getSubject();
        String email = token.getPayload().getEmail();
        String name = token.getPayload().get("name").toString();
        String profileImageUrl = token.getPayload().get("picture") != null
                ? token.getPayload().get("picture").toString()
                : null;
        return userService.getUser(googleId)
            .orElseGet(() -> userService.saveUser(
                UserEntity.builder()
                    .userStatus(UserStatus.TEMP)
                    .userType(UserType.USER)
                    .googleId(googleId)
                    .name(name)
                    .email(email)
                    .profileImageUrl(profileImageUrl)
                    .build()));
    }

}
