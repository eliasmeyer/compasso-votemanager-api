package br.com.compasso.votacao.api.services;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.models.Result;
import br.com.compasso.votacao.api.models.Session;
import br.com.compasso.votacao.api.models.Vote;
import br.com.compasso.votacao.api.repositories.VoteRepository;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class ResultService {
  
  private static final Long ZERO = 0L;
  @Autowired
  private VoteRepository voteRepository;
  
  public Result compute(Session session) {
    log.info("Computando os votos da sessao {[]}", session.getId());
    List<Vote> votes = voteRepository.findAllByIdSession(session.getId());
    Result result = new Result();
    result.setId(session.getId());
    //No vote registered 
    if (votes.isEmpty()) {
      result.setElectedOption(OptionVotation.INDETERMINADO);
      result.setTotalVotes(ZERO);
      log.warn("Nao ha votos para computar!");
    } else {
      Map.Entry<OptionVotation, Long> maxByOption = votes
          .parallelStream()
          .collect(collectingAndThen(
              groupingBy(Vote::getOptionVotation, counting()),
              map -> map.entrySet().stream().max(Map.Entry.comparingByValue()).get()));
    
      result.setElectedOption(maxByOption.getKey());
      result.setTotalVotes(maxByOption.getValue());
    }
    log.info("Voto da sessao {[]} computado com sucesso.");
    //TODO Post Service MESSAGE 
    return result;
  }
  
}
