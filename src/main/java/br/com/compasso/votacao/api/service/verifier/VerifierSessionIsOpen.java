package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("verifierSessionIsOpen")
@Slf4j
class VerifierSessionIsOpen implements VerifierCondition<Session> {
  
  @Override
  public void isOk(Session session) {
    if (session.getStatusSession() != StatusSession.ABERTO) {
      log.error("Session id [{}] is NOT Open", session.getId());
      throw new VotingTimeSessionExpiredException("Voting Time expired on session!");
    }
    log.debug("Session [{}] is OPEN", session.getId());
  }
}
