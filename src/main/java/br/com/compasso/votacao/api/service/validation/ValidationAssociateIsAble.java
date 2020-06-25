package br.com.compasso.votacao.api.service.validation;

import br.com.compasso.votacao.api.enums.StatusToVote;
import br.com.compasso.votacao.api.exception.AssociateUnableForVotingException;
import br.com.compasso.votacao.api.service.external.AssociateResponse;
import br.com.compasso.votacao.api.service.external.AssociateService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("validationAssociateIsAble")
class ValidationAssociateIsAble implements ValidationCondition<String> {
  
  @Autowired
  private AssociateService associateService;
  
  @Override
  public void isOk(String numberCpf) {
    AssociateResponse response = associateService.isAbleToVote(numberCpf);
    if (!(Objects.equals(response.getStatus(), StatusToVote.ABLE_TO_VOTE.toString()))) {
      throw new AssociateUnableForVotingException("Associate unable for voting");
    }
  }
}
