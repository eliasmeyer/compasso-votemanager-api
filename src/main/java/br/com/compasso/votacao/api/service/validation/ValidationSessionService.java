package br.com.compasso.votacao.api.service.validation;

import br.com.compasso.votacao.api.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ValidationSessionService {
  
  @Autowired
  @Qualifier("validationVoteInRangeDateTime")
  private ValidationCondition<Session> validationVoteInRangeDateTime;
  @Autowired
  @Qualifier("validationSessionIsOpen")
  private ValidationCondition<Session> validationSessionIsOpen;
  @Autowired
  @Qualifier("validationAssociateIsAble")
  private ValidationCondition<String> validationAssociateIsAble;
  
  
  public void validate(Session session, String cpfNumber) throws Exception {
    validationAssociateIsAble.isOk(cpfNumber);
    validationSessionIsOpen.isOk(session);
    validationVoteInRangeDateTime.isOk(session);
  }
}
