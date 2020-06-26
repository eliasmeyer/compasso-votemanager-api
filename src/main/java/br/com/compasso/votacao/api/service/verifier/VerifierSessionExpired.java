package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.model.Session;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component("validationVoteInRangeDateTime")
class VerifierSessionExpired implements VerifierCondition<Session> {
  
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
