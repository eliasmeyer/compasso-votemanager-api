package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
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
  @Autowired
  private SessionRepository sessionRepository;
  
  @Transactional(readOnly = true)
  public Optional<Session> findById(Long id) {
    Objects.requireNonNull(id);
    log.debug("Receive with id [{}]", id);
    return sessionRepository.findById(id);
  }
  
  @Transactional(readOnly = true)
  public List<Session> findAll() {
    log.debug("Find All");
    return sessionRepository.findAll();
  }
  
  public Session open(Long topicId, Long optionalVotingTime) {
    Objects.requireNonNull(topicId);
    //Check if Topic exists
    Topic topic = topicService.findById(topicId)
        .orElseThrow(() -> {
          log.error("Topic id [{}] not found", topicId);
          return new DataNotFoundException("Topic not found on " + topicId);
        });
  
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
    log.info("Receive vote with params: session_id [{}], cpf [{}]", sessionId, cpf);
  
    Session session = this.findById(sessionId)
        .orElseThrow(() -> {
          log.error("Session id [{}] not found", sessionId);
          return new DataNotFoundException("Session not found on " + sessionId);
        });
  
    managerSessionService.onVote(session, cpf, choice);
    log.info("Vote registered successfully");
  }
}
