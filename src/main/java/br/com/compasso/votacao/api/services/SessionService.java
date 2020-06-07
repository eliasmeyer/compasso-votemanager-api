package br.com.compasso.votacao.api.services;

import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exceptions.DataNotFoundException;
import br.com.compasso.votacao.api.models.Session;
import br.com.compasso.votacao.api.models.Topic;
import br.com.compasso.votacao.api.repositories.SessionRepository;
import br.com.compasso.votacao.api.utils.Constants;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionService {
  
  @Autowired
  private SessionRepository sessionRepository;
  
  @Transactional(readOnly = true)
  public Optional<Session> findById(Long id) {
    return sessionRepository.findById(id);
  }
  
  @Transactional(readOnly = true)
  public List<Session> findAll() {
    return sessionRepository.findAll();
  }
  
  public void saveAll(List<Session> sessions) {
    sessionRepository.saveAll(sessions);
  }
  
  @Transactional(readOnly = true)
  private void existSession(Long idSession) throws DataNotFoundException {
    Optional<Session> optionalSession = sessionRepository.findById(idSession);
    if (optionalSession.isPresent()) {
      throw new DataNotFoundException();
    }
  }
  
  public Session create(Topic topic, Long optionalVotingTime)
      throws DataNotFoundException {
    
    //Check if session exists
    this.existSession(topic.getId());
    
    Session newSession = new Session();
    LocalDateTime currentTime = LocalDateTime.now();
    
    newSession.setTopic(topic);
    newSession.setStatusSession(StatusSession.ABERTO);
    newSession.setDateTimeOpening(currentTime);
    
    newSession.setDateTimeClosing(
        currentTime.
            plusMinutes(
                Optional.ofNullable(optionalVotingTime).orElse(Constants.MINUTE_DEFAULT_SESSION)));
    newSession = sessionRepository.save(newSession);
    
    return newSession;
  }
  
  public void changeStatusToProcessing() {
    LocalDateTime localDateTime = LocalDateTime.now().withNano(Constants.NANO_OF_SECOND);
    List<Session> sessionsForComputing = sessionRepository
        .findAllWithDateTimeClosingAndStatusSessionOpen(localDateTime);
    
    sessionsForComputing.forEach(s -> s.setStatusSession(StatusSession.APURANDO));
    sessionRepository.saveAll(sessionsForComputing);
  }
  
  public List<Session> getCurrentSessionsToProcess() {
    return sessionRepository.findAllByStatusSession(StatusSession.APURANDO);
  }
  
  
  public boolean isOpen(Session session) {
    
    if (session.getStatusSession() != StatusSession.ABERTO) {
      return false;
    }
    
    final LocalDateTime dateTimeVote = LocalDateTime.now();
    Boolean isRange = (
        (dateTimeVote.isAfter(session.getDateTimeOpening()))
            && (dateTimeVote.isBefore(session.getDateTimeClosing()))
    );
    
    return isRange;
  }
  
}
