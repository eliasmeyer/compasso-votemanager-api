package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.model.Session;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("verifierSessionExpired")
@Slf4j
class VerifierSessionExpired implements VerifierCondition<Session> {
  
  @Override
  public void isOk(Session session) throws VotingTimeSessionExpiredException {
    LocalDateTime dateTimeVote = LocalDateTime.now();
    log.debug("Checking if current datetime [{}] is out time range in session", dateTimeVote);
    log.debug("Checking if current datetime [{}] is out time range in session", dateTimeVote);
    //Check if dateTimeVote is out time range 
    if (dateTimeVote.isBefore(session.getDateTimeOpening())
        || dateTimeVote.isAfter(session.getDateTimeClosing())) {
      throw new VotingTimeSessionExpiredException("Voting Time expired on session!");
    }
    log.info("Session [{}] not yet expired");
  }
}
