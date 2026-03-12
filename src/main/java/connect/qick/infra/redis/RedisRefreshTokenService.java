package connect.qick.infra.redis;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    public static final String REFRESH_TOKEN_PREFIX = "refresh:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void setRefreshToken(String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, "", ttl);
    }

    public void removeRefreshToken(String refreshToken) {
        checkRefreshToken(refreshToken);

        redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
    }

    public void checkRefreshToken(String refreshToken) {
        if(!redisTemplate.hasKey(REFRESH_TOKEN_PREFIX + refreshToken)) {
            throw new AuthException(AuthStatusCode.EXPIRED_TOKEN, "리프레시 토큰이 만료되었습니다.");
        }
    }
}
