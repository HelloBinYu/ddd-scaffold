package com.example.infrastructure.redis;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis 服务实现；RedissonClient 配置见 app 模块 config.RedisClientConfig
 */
@Service
public class RedisService implements IRedisService {

    private final RedissonClient redissonClient;

    public RedisService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> void setValue(String key, T value) {
        redissonClient.<T>getBucket(key).set(value);
    }

    @Override
    public <T> void setValue(String key, T value, Duration timeout) {
        redissonClient.<T>getBucket(key).set(value, timeout);
    }

    @Override
    public <T> T getValue(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public <T> boolean setNx(String key, T value, Duration timeout) {
        return redissonClient.<T>getBucket(key).setIfAbsent(value, timeout);
    }

    @Override
    public void remove(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public boolean isExists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    @Override
    public boolean expire(String key, Duration timeout) {
        return redissonClient.getBucket(key).expire(timeout);
    }

    @Override
    public void setAtomicLong(String key, long value) {
        redissonClient.getAtomicLong(key).set(value);
    }

    @Override
    public long getAtomicLong(String key) {
        return redissonClient.getAtomicLong(key).get();
    }

    @Override
    public long incr(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public long incrBy(String key, long delta) {
        return redissonClient.getAtomicLong(key).addAndGet(delta);
    }

    @Override
    public long decr(String key) {
        return redissonClient.getAtomicLong(key).decrementAndGet();
    }

    @Override
    public long decrBy(String key, long delta) {
        return redissonClient.getAtomicLong(key).addAndGet(-delta);
    }

    @Override
    public <K, V> RMap<K, V> getMap(String key) {
        return redissonClient.getMap(key);
    }

    @Override
    public <K, V> void addToMap(String key, K field, V value) {
        redissonClient.<K, V>getMap(key).fastPut(field, value);
    }

    @Override
    public <K, V> V getFromMap(String key, K field) {
        RMap<K, V> map = redissonClient.getMap(key);
        return map.get(field);
    }

    @Override
    public <T> RList<T> getList(String key) {
        return redissonClient.getList(key);
    }

    @Override
    public <T> RSet<T> getSet(String key) {
        return redissonClient.getSet(key);
    }

    @Override
    public <T> RScoredSortedSet<T> getSortedSet(String key) {
        return redissonClient.getScoredSortedSet(key);
    }

    @Override
    public <T> RBlockingQueue<T> getBlockingQueue(String key) {
        return redissonClient.getBlockingQueue(key);
    }

    @Override
    public RTopic getTopic(String key) {
        return redissonClient.getTopic(key);
    }

    @Override
    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }

    @Override
    public RLock getFairLock(String key) {
        return redissonClient.getFairLock(key);
    }

    @Override
    public RReadWriteLock getReadWriteLock(String key) {
        return redissonClient.getReadWriteLock(key);
    }

    @Override
    public RSemaphore getSemaphore(String key) {
        return redissonClient.getSemaphore(key);
    }

    @Override
    public RCountDownLatch getCountDownLatch(String key) {
        return redissonClient.getCountDownLatch(key);
    }

    @Override
    public <T> RBloomFilter<T> getBloomFilter(String key) {
        return redissonClient.getBloomFilter(key);
    }

}
