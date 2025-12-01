package com.ssafy.yogiattacku.security.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUtil {
    private final StringRedisTemplate redis;
    private static final String REFRESH_TOKEN_PREFIX = "rt:";

    private String buildKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    public void save(Long userId, String tokenValue, Duration ttl) {
        String key = buildKey(userId);
        ValueOperations<String, String> ops = redis.opsForValue();
        ops.set(key, tokenValue, ttl);
    }

    public Optional<String> findHash(Long userId) {
        String key = buildKey(userId);
        return Optional.ofNullable(redis.opsForValue().get(key));
    }

    public void delete(Long userId) {
        String key = buildKey(userId);
        redis.delete(key);
    }
}
