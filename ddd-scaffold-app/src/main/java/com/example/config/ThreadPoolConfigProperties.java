package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool.executor.config", ignoreInvalidFields = true)
public class ThreadPoolConfigProperties {

    /** 核心线程数 */
    private Integer corePoolSize = 20;
    /** 最大线程数 */
    private Integer maxPoolSize = 200;
    /** 空闲线程存活时间，单位秒 */
    private Long keepAliveTime = 60L;
    /** 最大队列数 */
    private Integer blockQueueSize = 5000;
    /** 线程名前缀，便于在日志和线程栈中定位 */
    private String threadNamePrefix = "biz-pool-";
    /*
     * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常。
     * DiscardPolicy：直接丢弃任务，但是不会抛出异常
     * DiscardOldestPolicy：将最早进入队列的任务删除，之后再尝试加入队列的任务被拒绝
     * CallerRunsPolicy：如果任务添加线程池失败，那么主线程自己执行该任务
     * */
    private String policy = "AbortPolicy";

}
