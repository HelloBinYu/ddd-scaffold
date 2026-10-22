/**
 * Redis 操作封装；IRedisService 由 RedisService 基于 Redisson 实现，供 adapter.repository 中的仓储实现使用
 * RedissonClient 的连接与序列化配置见 app 模块 config.RedisClientConfig
 */
package com.example.infrastructure.redis;
