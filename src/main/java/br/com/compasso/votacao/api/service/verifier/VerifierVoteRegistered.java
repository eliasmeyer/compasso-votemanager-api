package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.exception.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.service.VoteService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VerifierVoteRegistered implements VerifierCondition<VoteVerifierBean> {
  
  @Autowired
  private VoteService voteService;
  
  @Override
  public void isOk(VoteVerifierBean voteBean) {
    Optional<Vote> vote = voteService
        .findBySessionAndCpfNumber(voteBean.getSession(), voteBean.getCpfNumber());
    if (vote.isPresent()) {
      log.error("Vote Session id [{}], cpf [{}] already registered previously !");
      throw new VoteAlreadyRegisteredException("Vote already computed on session!");
    }
  }
}
