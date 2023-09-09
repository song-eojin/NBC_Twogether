package com.example.twogether.common.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRefreshToken {

    private final StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String refreshToken, String memberId) {
        redisTemplate.opsForValue().set(refreshToken, memberId);
        redisTemplate.expire(refreshToken, 7, TimeUnit.DAYS);
    }

    public boolean hasKey(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken));
    }

    public String getMemberId(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    public void removeRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }
}
