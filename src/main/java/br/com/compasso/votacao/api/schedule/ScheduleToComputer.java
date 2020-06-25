package br.com.compasso.votacao.api.schedule;

import br.com.compasso.votacao.api.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleToComputer {
  
  @Autowired
  private SessionService sessionService;
  
  @Scheduled(cron = "${schedule.session.toComputer.cron}")
  public void scheduleTaskComputing() {
    sessionService.close();
  }
  
}
