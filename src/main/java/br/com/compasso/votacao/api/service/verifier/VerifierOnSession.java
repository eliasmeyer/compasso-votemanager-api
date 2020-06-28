package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerifierOnSession {
  
  @Autowired
  @Qualifier("verifyIfAssociateIsAble")
  private VerifierCondition<String> verifyIfAssociateIsAble;
  @Autowired
  @Qualifier("verifierSessionIsOpen")
  private VerifierCondition<Session> verifierSessionIsOpen;
  @Autowired
  @Qualifier("verifierSessionExpired")
  private VerifierCondition<Session> verifierSessionExpired;
  
  
  public void validate(Session session, String cpfNumber) throws Exception {
    log.debug("Verifying cpf [{}] on session [{session}]", cpfNumber, session);
    verifyIfAssociateIsAble.isOk(cpfNumber);
    verifierSessionIsOpen.isOk(session);
    verifierSessionExpired.isOk(session);
    log.info("Vote [{}] verify with successfully on session [{session}]", cpfNumber, session);
  }
}
