package br.com.compasso.votacao.api.schedules;

import br.com.compasso.votacao.api.services.VoteManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleComputing {
  
  @Autowired
  private VoteManagerService voteManagerService;
  
  @Scheduled(cron = "${schedule.vote.computing.cron}")
  public void scheduleTaskComputing() {
    voteManagerService.computeAndClose();
  }
  
}
