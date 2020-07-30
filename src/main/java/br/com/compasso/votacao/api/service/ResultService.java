package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ResultService {
  
  @Autowired
  private VoteService voteService;
  private static final Long ZERO = 0L;
  
  public Result calculate(Session session) {
    Objects.requireNonNull(session);
    
    log.debug("Computing votes for Session [{}]", session.getId());
    List<Result> results = new ArrayList<>(voteService.countBySession(session));
    
    Result result = results
        .stream()
        .findFirst()
        .orElseGet(() -> {
          log.warn("There are no votes to compute in Session [{}]", session.getId());
          return new Result(OptionVotation.INDETERMINADO, ZERO);
        });
    return result;
  }
}
