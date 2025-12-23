package com.ssafy.yogiattacku.board.repository.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class BoardViewDistributedLockRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String KEY_FORMAT = "view::board::%s::user::%s::lock";

    public boolean lock(Long boardId, Long userId, Duration ttl) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key(boardId, userId), "", ttl);
        return Boolean.TRUE.equals(ok);
    }

    private String key(Long boardId, Long userId) {
        return KEY_FORMAT.formatted(boardId, userId);
    }
}
