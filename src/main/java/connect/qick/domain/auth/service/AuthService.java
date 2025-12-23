package connect.qick.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.auth.dto.response.UserResolveResult;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
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
        String googleId = googleIdToken.getPayload().getSubject();
        UserResolveResult resolveResult = getOrCreateUser(googleIdToken);
        String accessToken = jwtProvider.generateAccessToken(googleId, resolveResult.getUser().getUserType());
        String refreshToken = jwtProvider.generateRefreshToken(googleId, resolveResult.getUser().getUserType());
        return new LoginResponse(accessToken, refreshToken, resolveResult.getIsNewUser());
    }


    public GoogleIdToken verifyIdToken(final String idToken) {
        try {
            GoogleIdToken googleIdToken = idTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new AuthException(AuthStatusCode.INVALID_ID_TOKEN);
            }

            return googleIdToken;
        } catch (GeneralSecurityException | IOException e) {
            throw new AuthException(AuthStatusCode.INVALID_ID_TOKEN);
        }

    }

    public String refresh(String refreshToken) {
        Claims claims = jwtProvider.getClaims(refreshToken).getPayload();
        UserEntity user = userService.getUserByGoogleId(claims.getSubject())
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        return jwtProvider.generateAccessToken(claims.getSubject(), user.getUserType());
    }


    private UserResolveResult getOrCreateUser(GoogleIdToken token) {
        String googleId = token.getPayload().getSubject();
        Optional<UserEntity> opt = userService.getUserByGoogleId(googleId);
        if (opt.isPresent()) {
            return new UserResolveResult(opt.get());
        }
        String email = token.getPayload().getEmail();
        String name = token.getPayload().get("name").toString();
        return new UserResolveResult(true, userService.saveUser(
            UserEntity.builder()
                .userType(UserType.GUEST)
                .googleId(googleId)
                .name(name)
                .email(email)
                .build()));
        }
}
