package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class VerifierOnSession {
  
  @Autowired
  @Qualifier("validationVoteInRangeDateTime")
  private VerifierCondition<Session> validationVoteInRangeDateTime;
  @Autowired
  @Qualifier("validationSessionIsOpen")
  private VerifierCondition<Session> validationSessionIsOpen;
  @Autowired
  @Qualifier("validationAssociateIsAble")
  private VerifierCondition<String> validationAssociateIsAble;
  
  
  public void validate(Session session, String cpfNumber) throws Exception {
    validationAssociateIsAble.isOk(cpfNumber);
    validationSessionIsOpen.isOk(session);
    validationVoteInRangeDateTime.isOk(session);
  }
}
