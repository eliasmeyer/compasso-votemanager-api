package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class SessionService {
  
  @Autowired
  private TopicService topicService;
  @Autowired
  private ManagerSessionService managerSessionService;
  
  @Transactional(readOnly = true)
  public Optional<Session> findById(Long id) {
    Objects.requireNonNull(id);
    return managerSessionService.findById(id);
  }
  
  @Transactional(readOnly = true)
  public List<Session> findAll() {
    return managerSessionService.findAll();
  }
  
  public Session open(Long topicId, Long optionalVotingTime) {
    Objects.requireNonNull(topicId);
    //Check if Topic exists
    Topic topic = topicService.findById(topicId)
        .orElseThrow(() -> {
          log.error("Topic id [{}] not found", topicId);
          return new DataNotFoundException("Topic not found on " + topicId);
        });
  
    //Check if session exists
    if (managerSessionService.findById(topic.getId()).isPresent()) {
      log.error("Topic id [{}] with session already registered", topic.getId());
      throw new TopicWithExistingSessionException("Topic with session already registered");
    }
  
    Session session = managerSessionService.doOpen(topic, optionalVotingTime);
    log.info("Session created successfully");
    return session;
  }
  
  public void close() {
    log.debug("Starting harvest sessions to computer...");
    managerSessionService.onClose();
  }
  
  public void vote(Long sessionId, String cpf, OptionVotation choice) {
    Objects.requireNonNull(sessionId);
    Objects.requireNonNull(cpf);
    Objects.requireNonNull(choice);
    log.info("Receive vote with params: sessionId [{}], cpf [{}]", sessionId, cpf);
  
    Session session = managerSessionService.findById(sessionId)
        .orElseThrow(() -> {
          log.error("Session id [{}] not found", sessionId);
          return new DataNotFoundException("Session not found on " + sessionId);
        });
  
    managerSessionService.onVote(session, cpf, choice);
    log.info("Vote registered successfully");
  }
}
