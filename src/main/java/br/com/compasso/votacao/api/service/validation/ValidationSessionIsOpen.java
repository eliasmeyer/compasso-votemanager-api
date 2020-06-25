package br.com.compasso.votacao.api.service.validation;

import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.model.Session;
import org.springframework.stereotype.Component;

@Component("validationSessionIsOpen")
class ValidationSessionIsOpen implements ValidationCondition<Session> {
  
  @Override
  public void isOk(Session session) throws VotingTimeSessionExpiredException {
    if (session.getStatusSession() != StatusSession.ABERTO) {
      throw new VotingTimeSessionExpiredException("Voting Time expired on session!");
    }
  }
}
