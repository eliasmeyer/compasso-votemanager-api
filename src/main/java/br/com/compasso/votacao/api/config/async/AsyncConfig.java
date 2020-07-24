package br.com.compasso.votacao.api.config.async;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
  
  @Value("${compasso.votemanager.closeSessionThread.corePoolSize}")
  private int closeSessionThreadCoreSize;
  @Value("${compasso.votemanager.closeSessionThread.maxPoolSize}")
  private int closeSessionThreadMaxSize;
  @Value("${compasso.votemanager.closeSessionThread.queueCapacity}")
  private int closeSessionThreadQueueCapacity;
  @Value("${compasso.votemanager.closeSessionThread.threadNamePrefix}")
  private String closeSessionThreadNamePrefix;
  
  @Bean(name = "closeSessionThread")
  public Executor getCloseSessionThread() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(closeSessionThreadCoreSize);
    executor.setMaxPoolSize(closeSessionThreadMaxSize);
    executor.setQueueCapacity(closeSessionThreadQueueCapacity);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix(closeSessionThreadNamePrefix);
    executor.initialize();
    return executor;
  }
}
