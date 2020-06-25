package br.com.compasso.votacao.api.service.validation;

import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.model.Session;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component("validationVoteInRangeDateTime")
class ValidationVoteInRangeDateTime implements ValidationCondition<Session> {
  
  @Override
  public void isOk(Session session) throws VotingTimeSessionExpiredException {
    LocalDateTime dateTimeVote = LocalDateTime.now();
    //Check if dateTimeVote is out time range 
    if (dateTimeVote.isBefore(session.getDateTimeOpening())
        || dateTimeVote.isAfter(session.getDateTimeClosing())) {
      throw new VotingTimeSessionExpiredException("Voting Time expired on session!");
    }
  }
}
