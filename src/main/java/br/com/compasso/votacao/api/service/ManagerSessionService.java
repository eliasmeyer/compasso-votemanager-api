package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
import br.com.compasso.votacao.api.service.validation.ValidationSessionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ManagerSessionService {
  
  private static final Long MINUTE_DEFAULT_SESSION = 1L;
  private static final int ZERO_NANO_OF_SECOND = 0;
  
  @Autowired
  private VoteService voteService;
  @Autowired
  private ResultService resultService;
  @Autowired
  private SessionRepository sessionRepository;
  @Autowired
  private ValidationSessionService validationSessionService;
  
  protected Session doOpen(Topic topic, Long optionalVotingTime)
      throws TopicWithExistingSessionException {
    
    //Check if session exists
    if (sessionRepository.findById(topic.getId()).isPresent()) {
      throw new TopicWithExistingSessionException("Topic with session already registered");
    }
    
    Session sessionNew = new Session();
    LocalDateTime currentTime = LocalDateTime.now();
    sessionNew.setTopic(topic);
    sessionNew.setStatusSession(StatusSession.ABERTO);
    sessionNew.setDateTimeOpening(currentTime);
    sessionNew.setDateTimeClosing(
        currentTime.
            plusMinutes(
                Optional.ofNullable(optionalVotingTime).orElse(MINUTE_DEFAULT_SESSION)));
    
    return sessionRepository.save(sessionNew);
  }
  
  protected void onVote(Session session, String numberCpf, OptionVotation choice)
      throws Exception {
    
    validationSessionService.validate(session, numberCpf);
    voteService.register(session, numberCpf, choice);
  }
  
  protected void onClose() {
    LocalDateTime localDateTime = LocalDateTime.now().withNano(ZERO_NANO_OF_SECOND);
    List<Session> sessionsToComputer = sessionRepository
        .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(localDateTime);
    
    sessionsToComputer.parallelStream().forEach(s -> s.setStatusSession(StatusSession.APURANDO));
    sessionRepository.saveAll(sessionsToComputer);
    sessionRepository.flush();
    
    sessionsToComputer
        .parallelStream()
        .forEach(currentSession -> {
          List<Result> results = voteService.countBySession(currentSession);
          resultService.save(currentSession.getTopic(), results);
          currentSession.setStatusSession(StatusSession.FECHADO);
        });
    
    sessionRepository.saveAll(sessionsToComputer);
    //TODO POSTING RESULT IN MESSAGE QUEUE 
  }
}
