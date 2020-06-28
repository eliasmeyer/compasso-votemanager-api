package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
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
    log.info("Receive with id [{}]", id);
    return sessionRepository.findById(id);
  }
  
  @Transactional(readOnly = true)
  public List<Session> findAll() {
    return sessionRepository.findAll();
  }
  
  @Transactional(readOnly = true)
  public List<Session> findAllByStatusSession(StatusSession statusSession) {
    Objects.requireNonNull(statusSession);
    return sessionRepository.findAllByStatusSession(statusSession);
  }
  
  public Session open(Long topicId, Long optionalVotingTime)
      throws DataNotFoundException, TopicWithExistingSessionException {
    
    Objects.requireNonNull(topicId);
    //Check if Topic exists
    Topic topic = topicService.findById(topicId)
        .orElseThrow(() -> new DataNotFoundException("Topic not found on " + topicId));
    
    return managerSessionService.doOpen(topic, optionalVotingTime);
  }
  
  public void close() {
    log.debug("Starting harvest sessions to computer...");
    managerSessionService.onClose();
  }
  
  public void vote(Long sessionId, String cpf, OptionVotation choice)
      throws Exception {
    log.info("Receive vote with params: session_id [{}], cpf [{}]", sessionId, cpf);
    Objects.requireNonNull(sessionId);
    Objects.requireNonNull(cpf);
    Objects.requireNonNull(choice);
    
    Session session = this.findById(sessionId)
        .orElseThrow(() -> new DataNotFoundException("Session not found on " + sessionId));
    
    managerSessionService.onVote(session, cpf, choice);
  }
}
