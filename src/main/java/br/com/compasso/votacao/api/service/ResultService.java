package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Topic;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ResultService {
  
  private static final Long ZERO = 0L;
  
  public Result computer(Topic topic, List<Result> results) {
    Objects.requireNonNull(results);
    Objects.requireNonNull(topic);
    log.debug("Computing vote for Pauta [{}]", topic.getId());
    Result result = results
        .stream()
        .findFirst()
        .orElseGet(() -> {
          log.debug("There are no votes to compute in Pauta [{}]", topic.getId());
          return new Result(OptionVotation.INDETERMINADO, ZERO);
        });
    topic.setResult(result);
    log.info("Result successfully computed to Pauta [{}]", topic.getId());
    return result;
  }
}
