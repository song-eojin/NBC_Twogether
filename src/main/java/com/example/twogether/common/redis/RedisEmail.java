package com.example.twogether.common.redis;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisEmail {

    private final StringRedisTemplate redisTemplate;

    private static final String CERTIFICATION_NUNBER = "certificationNumber";
    private static final String VERIFIED = "verified";

    public void saveCertification(String email, String certificationNumber) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(email, CERTIFICATION_NUNBER, certificationNumber);
        hashOperations.put(email, VERIFIED, "not yet verified");

        redisTemplate.expire(email, 3, TimeUnit.MINUTES);
    }

    public String getCertificationNumber(String email) {
        return (String) redisTemplate.opsForHash().get(email, CERTIFICATION_NUNBER);
    }

    public void removeCertificationNumber(String email) {
        redisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }

    public void updateVerified(String email) {
        redisTemplate.opsForHash().put(email, VERIFIED, VERIFIED);
    }

    public boolean isVerified(String email) {
        return Objects.equals(redisTemplate.opsForHash().get(email, VERIFIED), VERIFIED);
    }
}
