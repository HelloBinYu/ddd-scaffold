package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 连接配置，对应 application.yml 中的 redis.sdk.config
 */
@Data
@ConfigurationProperties(prefix = "redis.sdk.config", ignoreInvalidFields = true)
public class RedisClientConfigProperties {

    /** host:ip */
    private String host = "127.0.0.1";
    /** 端口 */
    private int port = 6379;
    /** 账密；未设置密码时留空 */
    private String password;
    /** 数据库编号 */
    private int database = 0;
    /** 连接池大小，默认为64 */
    private int poolSize = 64;
    /** 连接池的最小空闲连接数，默认为10 */
    private int minIdleSize = 10;
    /** 连接的最大空闲时间（单位：毫秒），超过该时间的空闲连接将被关闭，默认为10000 */
    private int idleTimeout = 10000;
    /** 连接超时时间（单位：毫秒），默认为10000 */
    private int connectTimeout = 10000;
    /** 命令执行超时时间（单位：毫秒），默认为3000 */
    private int timeout = 3000;
    /** 命令失败重试次数，默认为3 */
    private int retryAttempts = 3;
    /** 定期检查连接是否可用的时间间隔（单位：毫秒），默认为30000 */
    private int pingInterval = 30000;
    /** 是否保持长连接，默认为true */
    private boolean keepAlive = true;
    /**
     * 是否延迟初始化，默认为true
     * true：启动时不连接 Redis，首次执行命令时才建立连接，Redis 不可用时应用仍可启动（便于本地开发与单元测试）
     * false：启动时立即连接，连接失败则启动失败，生产环境可按需开启以尽早暴露问题
     */
    private boolean lazyInitialization = true;

}
