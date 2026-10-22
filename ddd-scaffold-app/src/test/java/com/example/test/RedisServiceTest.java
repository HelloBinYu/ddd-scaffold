package com.example.test;

import com.example.infrastructure.redis.IRedisService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 需要可用的 Redis，设置环境变量 REDIS_TEST=true 后执行（默认连接 dev 环境 127.0.0.1:16379）
 */
@Slf4j
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "REDIS_TEST", matches = "true")
class RedisServiceTest {

    private static final String KEY_PREFIX = "redis_service_test:";

    @Autowired
    private IRedisService redisService;

    @AfterEach
    void cleanUp() {
        for (String key : new String[]{"value", "nx", "counter", "map", "lock"}) {
            redisService.remove(KEY_PREFIX + key);
        }
    }

    @Test
    void valueRoundTrip() {
        String key = KEY_PREFIX + "value";
        redisService.setValue(key, new User("10001", "xiaofuge"), Duration.ofMinutes(1));

        User user = redisService.getValue(key);
        assertEquals(new User("10001", "xiaofuge"), user);
        assertTrue(redisService.isExists(key));

        redisService.remove(key);
        assertFalse(redisService.isExists(key));
    }

    @Test
    void setNx() {
        String key = KEY_PREFIX + "nx";
        assertTrue(redisService.setNx(key, "1", Duration.ofMinutes(1)));
        assertFalse(redisService.setNx(key, "2", Duration.ofMinutes(1)));
        assertEquals("1", redisService.getValue(key));
    }

    @Test
    void counter() {
        String key = KEY_PREFIX + "counter";
        redisService.setAtomicLong(key, 10);
        assertEquals(11, redisService.incr(key));
        assertEquals(16, redisService.incrBy(key, 5));
        assertEquals(15, redisService.decr(key));
        assertEquals(12, redisService.decrBy(key, 3));
        assertEquals(12, redisService.getAtomicLong(key));
    }

    @Test
    void map() {
        String key = KEY_PREFIX + "map";
        redisService.addToMap(key, "name", "xiaofuge");
        assertEquals("xiaofuge", redisService.getFromMap(key, "name"));
    }

    @Test
    void lock() throws InterruptedException {
        RLock lock = redisService.getLock(KEY_PREFIX + "lock");
        assertTrue(lock.tryLock(1, 10, TimeUnit.SECONDS));
        try {
            assertTrue(lock.isHeldByCurrentThread());
        } finally {
            lock.unlock();
        }
        assertFalse(lock.isLocked());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String userId;
        private String userName;
    }

}
