package com.ssafy.yogiattacku.board.repository.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardViewCountRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "view::board::%s::count";

    public Long read(Long boardId) {
        String result = redisTemplate.opsForValue().get(key(boardId));
        return result == null ? 0L : Long.parseLong(result);
    }

    public boolean setIfAbsent(Long boardId, long seed) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key(boardId), String.valueOf(seed));
        return Boolean.TRUE.equals(ok);
    }

    public Long increase(Long boardId) {
        return redisTemplate.opsForValue().increment(key(boardId));
    }

    private String key(Long boardId) {
        return KEY_FORMAT.formatted(boardId);
    }
}
