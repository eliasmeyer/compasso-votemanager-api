package br.com.compasso.votacao.api.service;

import static java.util.stream.Collectors.toList;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
import br.com.compasso.votacao.api.service.verifier.VerifierOnSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
class ManagerSessionService {
  
  private static final Long MINUTE_DEFAULT_SESSION = 1L;
  private static final int ZERO = 0;
  
  @Value("${compasso.votemanager.processSession.chunk}")
  private Integer chunkSize;
  @Autowired
  private VoteService voteService;
  @Autowired
  private SessionRepository sessionRepository;
  @Autowired
  private VerifierOnSession verifierOnSession;
  @Autowired
  private ProcessSessionService processSessionService;
  @Autowired
  private PublisherService publisherService;
  
  @Transactional(readOnly = true)
  public Optional<Session> findById(Long id) {
    log.debug("Find Session with id [{}]", id);
    return sessionRepository.findById(id);
  }
  
  @Transactional(readOnly = true)
  public List<Session> findAll() {
    log.debug("Find All");
    return sessionRepository.findAll();
  }
  
  protected Session doOpen(Topic topic, Long optionalVotingTime) {
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
  
  @Async("closeSessionThread")
  protected void onClose() {
    log.info("Harvesting sessions for computer");
    LocalDateTime localDateTime = LocalDateTime.now().withNano(ZERO);
    
    int pageCurrent = ZERO;
    PageRequest pageRequest = PageRequest.of(ZERO, chunkSize);
    Slice<Session> sessionsToComputer = sessionRepository
        .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(localDateTime, pageRequest);
    
    if (sessionsToComputer.isEmpty()) {
      log.info("No session to process!");
      return;
    }
    
    UUID transaction = UUID.randomUUID();
    
    while (sessionsToComputer.hasContent()) {
      log.info("Processing transaction Sessions batch: [{}]", transaction);
      log.info("TRANSACTION: [{}]. Found [{}] Session's page with chunk size [{}]", transaction,
          sessionsToComputer.getNumberOfElements(),
          chunkSize);
      sessionsToComputer.getContent().forEach(s -> s.setStatusSession(StatusSession.APURANDO));
      sessionRepository.saveAll(sessionsToComputer.getContent());
      sessionRepository.flush();
      log.info("TRANSACTION: [{}]. Changed [{}] Sessions's status for APURANDO", transaction,
          sessionsToComputer.getContent().size());
      
      List<Session> sessionsProcessed = new ArrayList<>();
      for (Session current : sessionsToComputer.getContent()) {
        Session session = processSessionService.compute(current);
        sessionsProcessed.add(session);
      }
      
      List<Session> sessionsComputed = sessionsProcessed.stream()
          .filter(item -> item.getStatusSession() == StatusSession.FECHADO)
          .collect(toList());
      
      sessionRepository.saveAll(sessionsComputed);
      sessionRepository.flush();
      log.info("TRANSACTION: [{}]. Sessions saved successfully", transaction);
      List<Topic> resultToPublish = sessionsComputed.stream().map(Session::getTopic)
          .collect(toList());
      
      log.info("TRANSACTION: [{}]. Starting publishing results...", transaction);
      publisherService.publishAll(resultToPublish);
      log.info("TRANSACTION: [{}]. Finished publish results...", transaction);
      
      pageRequest = PageRequest.of(++pageCurrent, chunkSize);
      sessionsToComputer = sessionRepository
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(localDateTime, pageRequest);
    }
  }
}
