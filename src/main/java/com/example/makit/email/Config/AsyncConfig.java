package com.example.makit.email.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본적으로 실행 대기 중인 쓰레드 수
        executor.setMaxPoolSize(10); // 최대 생성할 쓰레드 수
        executor.setQueueCapacity(500); // 쓰레드 대기열 용량
        executor.setThreadNamePrefix("AsyncExecutor-"); // 쓰레드 이름
        executor.initialize();
        return executor;
    }
}