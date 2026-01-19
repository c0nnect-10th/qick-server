package connect.qick.infra.redis;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistService {

    public static String BLACKLIST_PREFIX = "blacklist:access:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void setBlacklist(String accessToken, Duration ttl) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, 1, ttl);
    }

    public void checkBlacklisted(String accessToken) {
        if(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken)) {
            throw new AuthException(AuthStatusCode.TOKEN_BLACKLISTED);
        }
    }
}
