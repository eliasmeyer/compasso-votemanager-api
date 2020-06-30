package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
import br.com.compasso.votacao.api.service.verifier.VerifierOnSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
  private VerifierOnSession verifierOnSession;
  
  protected Session doOpen(Topic topic, Long optionalVotingTime) {
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
  
  protected void onVote(Session session, String numberCpf, OptionVotation choice) {
    verifierOnSession.validate(session, numberCpf);
    voteService.register(session, numberCpf, choice);
  }
  
  protected void onClose() {
    log.info("Harvesting sessions for computer");
    LocalDateTime localDateTime = LocalDateTime.now().withNano(ZERO_NANO_OF_SECOND);
    List<Session> sessionsToComputer = sessionRepository
        .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(localDateTime);
    
    if (sessionsToComputer.isEmpty()) {
      log.info("No session to process!");
      return;
    }
    log.info("Found [{}] Sessions to computer", sessionsToComputer.size());
    sessionsToComputer.parallelStream().forEach(s -> s.setStatusSession(StatusSession.APURANDO));
    sessionRepository.saveAll(sessionsToComputer);
    sessionRepository.flush();
    
    sessionsToComputer
        .parallelStream()
        .forEach(currentSession -> {
          List<Result> results = voteService.countBySession(currentSession);
          Result result = resultService.computer(currentSession.getTopic(), results);
          currentSession.getTopic().setResult(result);
          currentSession.setStatusSession(StatusSession.FECHADO);
        });
    
    sessionRepository.saveAll(sessionsToComputer);
    log.info("Sessions successfully closed");
    //TODO POSTING RESULT IN MESSAGE QUEUE 
  }
}
