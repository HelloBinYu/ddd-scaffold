package com.example.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 客户端，使用 Redisson <a href="https://github.com/redisson/redisson">Redisson</a>
 */
@Configuration
@EnableConfigurationProperties(RedisClientConfigProperties.class)
public class RedisClientConfig {

    /**
     * 容器关闭时 Spring 会自动调用 shutdown() 释放连接
     */
    @Bean("redissonClient")
    public RedissonClient redissonClient(RedisClientConfigProperties properties) {
        Config config = new Config();
        // 值使用 JSON 序列化并携带类型信息，可直接反序列化为原对象，也便于在 Redis 客户端中查看
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.setLazyInitialization(properties.isLazyInitialization());

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(StringUtils.defaultIfBlank(properties.getPassword(), null))
                .setDatabase(properties.getDatabase())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setTimeout(properties.getTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());

        return Redisson.create(config);
    }

}
