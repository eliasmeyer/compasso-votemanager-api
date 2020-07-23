package br.com.compasso.votacao.api.config.scheduler;

import br.com.compasso.votacao.api.service.SessionService;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnExpression("${compasso.votemanager.scheduler.enable:true}")
public class SchedulerConfig {
  
  @Autowired
  private SessionService sessionService;
  @Value("${compasso.votemanager.scheduler.enable}")
  private boolean enableScheduler;
  @Value("${compasso.votemanager.scheduler.cron}")
  private String cron;
  
  @Scheduled(cron = "${compasso.votemanager.scheduler.cron}")
  public void scheduledJob() {
    sessionService.close();
  }
  
  @PostConstruct
  public void doLog() {
    log.info("Enable Scheduler: [{}]", enableScheduler);
    log.info("Cron scheduled : [{}]", cron);
  }
}

