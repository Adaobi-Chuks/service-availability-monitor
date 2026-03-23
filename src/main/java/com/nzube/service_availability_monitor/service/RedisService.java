package com.nzube.service_availability_monitor.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.nzube.service_availability_monitor.enums.ServiceStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ─── Service Status ───────────────────────────────────────────

    private static final String SERVICE_STATUS_KEY = "service:status:";
    private static final Duration STATUS_TTL = Duration.ofDays(1);

    public void saveLastKnownStatus(Long serviceId, ServiceStatus status) {
        String key = SERVICE_STATUS_KEY + serviceId;
        redisTemplate.opsForValue().set(key, status.name(), STATUS_TTL);
        log.debug("Saved status {} for service {}", status, serviceId);
    }

    public ServiceStatus getLastKnownStatus(Long serviceId) {
        String key = SERVICE_STATUS_KEY + serviceId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null)
            return null;
        return ServiceStatus.valueOf(value.toString());
    }

    public void deleteLastKnownStatus(Long serviceId) {
        redisTemplate.delete(SERVICE_STATUS_KEY + serviceId);
    }

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
