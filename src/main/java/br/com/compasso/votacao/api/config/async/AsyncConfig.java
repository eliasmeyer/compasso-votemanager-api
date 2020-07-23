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
  
  @Value("${compasso.votemanager.workerSessionThread.corePoolSize}")
  private int workerSessionThreadCoreSize;
  @Value("${compasso.votemanager.workerSessionThread.maxPoolSize}")
  private int workerSessionThreadMaxSize;
  @Value("${compasso.votemanager.workerSessionThread.queueCapacity}")
  private int workerSessionThreadQueueCapacity;
  @Value("${compasso.votemanager.workerSessionThread.threadNamePrefix}")
  private String workerSessionThreadNamePrefix;
  
  @Value("${compasso.votemanager.closeSessionThread.corePoolSize}")
  private int closeSessionThreadCoreSize;
  @Value("${compasso.votemanager.closeSessionThread.maxPoolSize}")
  private int closeSessionThreadMaxSize;
  @Value("${compasso.votemanager.closeSessionThread.queueCapacity}")
  private int closeSessionThreadQueueCapacity;
  @Value("${compasso.votemanager.closeSessionThread.threadNamePrefix}")
  private String closeSessionThreadNamePrefix;
  
  @Bean(name = "workerSessionThread")
  public Executor getWorkerSessionThread() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(workerSessionThreadCoreSize);
    executor.setMaxPoolSize(workerSessionThreadMaxSize);
    executor.setQueueCapacity(workerSessionThreadQueueCapacity);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix(workerSessionThreadNamePrefix);
    executor.initialize();
    return executor;
  }
  
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
