package com.doclarify.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "ocrExecutor")
    public Executor ocrExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(4);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setQueueCapacity(50);
        threadPoolTaskExecutor.setThreadNamePrefix("doc-processor-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("default-");
        executor.initialize();
        return executor;
    }
}
