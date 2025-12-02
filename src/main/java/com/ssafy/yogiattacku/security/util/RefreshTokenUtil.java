package com.ssafy.yogiattacku.security.util;

import com.ssafy.yogiattacku.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUtil {
    private final StringRedisTemplate redis;
    private static final String REFRESH_TOKEN_PREFIX = "rt:";

    private String buildKey(String tokenHash) {
        return REFRESH_TOKEN_PREFIX + tokenHash;
    }

    public void save(Long userId, String tokenHash, Duration ttl) {
        String key = buildKey(tokenHash);
        redis.opsForValue().set(key, String.valueOf(userId), ttl);
    }

    public Optional<Long> findUserIdByTokenHash(String tokenHash) {
        String key = buildKey(tokenHash);
        String value = redis.opsForValue().get(key);
        if(value == null) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(value));
    }

    public void delete(String tokenHash) {
        String key = buildKey(tokenHash);
        redis.delete(key);
    }
}
