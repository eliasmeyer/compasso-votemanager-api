package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.TopicRepository;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
class ResultService {
  
  private static final Long ZERO = 0L;
  
  @Autowired
  private TopicRepository topicRepository;
  
  public Result save(Topic topic, List<Result> results) {
    Objects.requireNonNull(results);
    Objects.requireNonNull(topic);
    
    Result result = results
        .stream()
        .findFirst()
        .orElse(new Result(OptionVotation.INDETERMINADO, ZERO));
    
    topic.setResult(result);
    topicRepository.save(topic);
    return result;
  }
}
