package com.nzube.service_availability_monitor.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ─── Login Rate Limiting ──────────────────────────────────────

    private static final String LOGIN_ATTEMPT_RATE_LIMIT_KEY = "login:attempts:";
    private static final String LOGIN_BLOCK_KEY = "login:blocked:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final Duration LOGIN_ATTEMPT_TTL = Duration.ofMinutes(15);
    private static final Duration LOGIN_BLOCK_TTL = Duration.ofMinutes(30);

    public boolean isLoginBlocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(LOGIN_BLOCK_KEY + email));
    }

    public void recordFailedLoginAttempt(String email) {
        String key = LOGIN_ATTEMPT_RATE_LIMIT_KEY + email;
        Long attempts = redisTemplate.opsForValue().increment(key);

        // set TTL on first attempt
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, LOGIN_ATTEMPT_TTL);
        }

        // block the user if max attempts reached
        if (attempts != null && attempts >= MAX_LOGIN_ATTEMPTS) {
            redisTemplate.opsForValue().set(LOGIN_BLOCK_KEY + email, "1", LOGIN_BLOCK_TTL);
            redisTemplate.delete(key); // clear attempt counter
            log.warn("Login blocked for {} after {} failed attempts", email, MAX_LOGIN_ATTEMPTS);
        }
    }

    public void clearLoginAttempts(String email) {
        redisTemplate.delete(LOGIN_ATTEMPT_RATE_LIMIT_KEY + email);
        redisTemplate.delete(LOGIN_BLOCK_KEY + email);
    }
}
