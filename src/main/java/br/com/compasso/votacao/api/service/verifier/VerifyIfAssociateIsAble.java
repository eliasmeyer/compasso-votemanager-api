package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.enums.StatusToVote;
import br.com.compasso.votacao.api.exception.AssociateUnableForVotingException;
import br.com.compasso.votacao.api.service.external.AssociateResponse;
import br.com.compasso.votacao.api.service.external.AssociateService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("verifyIfAssociateIsAble")
@Slf4j
class VerifyIfAssociateIsAble implements VerifierCondition<String> {
  
  @Autowired
  private AssociateService associateService;
  
  @Override
  public void isOk(String numberCpf) {
    log.debug("Checking if cpf [{}] is able...", numberCpf);
    AssociateResponse response = associateService.isAbleToVote(numberCpf);
    log.debug("ASSOCIATE RESPONSE : [{}]", response);
    if (!(Objects.equals(response.getStatus(), StatusToVote.ABLE_TO_VOTE.toString()))) {
      throw new AssociateUnableForVotingException("Associate unable for voting");
    }
    log.info("CPF [{}] able to vote!", numberCpf);
  }
}
