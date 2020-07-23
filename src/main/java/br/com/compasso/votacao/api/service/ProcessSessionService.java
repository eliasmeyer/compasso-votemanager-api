package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ProcessSessionService {
  
  @Autowired
  private ResultService resultService;
  @Autowired
  private TopicService topicService;
  
  public Session compute(Session session) {
    final Session sessionComputed = session;
    try {
      log.info("Task computation for session [{}] started", sessionComputed.getId());
      Result result = resultService.calculate(sessionComputed);
      Topic topic = topicService.findById(sessionComputed.getId()).get();
      topic.setResult(result);
      sessionComputed.setTopic(topic);
      session.setStatusSession(StatusSession.FECHADO);
      log.info("Task computation for session [{}] finished", sessionComputed.getId());
    } catch (Exception ex) {
      sessionComputed.setStatusSession(StatusSession.ABERTO);
      log.error("ERROR PROCESSING TASK COMPUTATION ON SESSION [{}].", sessionComputed.getId(), ex);
    }
    return sessionComputed;
  }
}
