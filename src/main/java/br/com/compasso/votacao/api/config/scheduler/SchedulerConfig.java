package br.com.compasso.votacao.api.config.scheduler;

import br.com.compasso.votacao.api.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
  
  @Value("${compasso.votemanager.scheduler.enable}")
  private boolean isEnabled;
  
  @Autowired
  private SessionService sessionService;
  
  @Scheduled(cron = "${compasso.votemanager.scheduler.cron}")
  public void scheduledJob() {
    if (isEnabled) {
      sessionService.close();
    }
  }
}
