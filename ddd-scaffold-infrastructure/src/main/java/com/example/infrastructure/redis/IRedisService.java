package com.example.infrastructure.redis;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;

import java.time.Duration;

/**
 * Redis 服务；基于 Redisson 封装常用操作，更复杂的场景可直接注入 RedissonClient 使用
 */
public interface IRedisService {

    // ---------------------------------------------------------------- 字符串 / 对象

    /** 设置值 */
    <T> void setValue(String key, T value);

    /** 设置值并指定过期时间 */
    <T> void setValue(String key, T value, Duration timeout);

    /** 获取值，不存在时返回 null */
    <T> T getValue(String key);

    /** key 不存在时设置值（SET NX），设置成功返回 true；可用于防重复提交 */
    <T> boolean setNx(String key, T value, Duration timeout);

    /** 删除 key */
    void remove(String key);

    /** 判断 key 是否存在 */
    boolean isExists(String key);

    /** 设置过期时间，key 不存在时返回 false */
    boolean expire(String key, Duration timeout);

    // ---------------------------------------------------------------- 计数器

    /** 设置计数器的值 */
    void setAtomicLong(String key, long value);

    /** 获取计数器的值，不存在时返回 0 */
    long getAtomicLong(String key);

    /** 自增 1，返回自增后的值 */
    long incr(String key);

    /** 自增指定步长，返回自增后的值 */
    long incrBy(String key, long delta);

    /** 自减 1，返回自减后的值 */
    long decr(String key);

    /** 自减指定步长，返回自减后的值 */
    long decrBy(String key, long delta);

    // ---------------------------------------------------------------- 集合

    /** 获取 Map（Redis Hash） */
    <K, V> RMap<K, V> getMap(String key);

    /** 向 Map 中放入键值 */
    <K, V> void addToMap(String key, K field, V value);

    /** 从 Map 中获取值 */
    <K, V> V getFromMap(String key, K field);

    /** 获取 List */
    <T> RList<T> getList(String key);

    /** 获取 Set */
    <T> RSet<T> getSet(String key);

    /** 获取有序集合（Redis ZSet） */
    <T> RScoredSortedSet<T> getSortedSet(String key);

    /** 获取阻塞队列 */
    <T> RBlockingQueue<T> getBlockingQueue(String key);

    /** 获取发布订阅主题 */
    RTopic getTopic(String key);

    // ---------------------------------------------------------------- 分布式锁与同步器

    /**
     * 获取可重入锁；推荐用法：
     * <pre>
     * RLock lock = redisService.getLock("lock_key");
     * if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
     *     try { ... } finally { lock.unlock(); }
     * }
     * </pre>
     */
    RLock getLock(String key);

    /** 获取公平锁；按请求顺序获取锁 */
    RLock getFairLock(String key);

    /** 获取读写锁 */
    RReadWriteLock getReadWriteLock(String key);

    /** 获取信号量；可用于限流、库存扣减等场景 */
    RSemaphore getSemaphore(String key);

    /** 获取闭锁 */
    RCountDownLatch getCountDownLatch(String key);

    /** 获取布隆过滤器；使用前需调用 tryInit(expectedInsertions, falseProbability) 初始化 */
    <T> RBloomFilter<T> getBloomFilter(String key);

}
