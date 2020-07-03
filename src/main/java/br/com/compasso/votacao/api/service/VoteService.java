package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.repository.VoteRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class VoteService {
  
  @Autowired
  private SessionService sessionService;
  @Autowired
  private VoteRepository voteRepository;
  
  public List<Vote> findAllBySessionId(Long sessionId) throws DataNotFoundException {
    Objects.requireNonNull(sessionId);
    Session session = sessionService.findById(sessionId)
        .orElseThrow(() -> {
          log.error("Session id [{}] not found", sessionId);
          return new DataNotFoundException("Session not found on " + sessionId);
        });
    
    return this.findAllBySession(session);
  }
  
  public List<Vote> findAllBySession(Session session) {
    Objects.requireNonNull(session);
    log.debug("Find All by Session");
    return voteRepository.findAllBySession(session);
  }
  
  public Vote findBySessionIdAndCpfNumber(Long sessionId, String cpfNumber)
      throws DataNotFoundException {
    Objects.requireNonNull(sessionId);
    Objects.requireNonNull(cpfNumber);
  
    Session session = sessionService.findById(sessionId)
        .orElseThrow(() -> {
          log.error("Session id [{}] not found", sessionId);
          return new DataNotFoundException("Session not found on " + sessionId);
        });
    Optional<Vote> vote = voteRepository.findBySessionAndCpfNumber(session, cpfNumber);
    return vote.orElseThrow(() -> {
      log.error("Vote Session id [{}], cpf [{}] not found", sessionId, cpfNumber);
      return new DataNotFoundException("Vote not found!");
    });
  }
  
  @Transactional
  public Vote register(Session session, String numberCpf, OptionVotation choice)
      throws VoteAlreadyRegisteredException {
    Objects.requireNonNull(session);
    Objects.requireNonNull(numberCpf);
    Objects.requireNonNull(choice);
    
    Optional<Vote> vote = voteRepository.findBySessionAndCpfNumber(session, numberCpf);
    if (vote.isPresent()) {
      log.error("Vote Session id [{}], cpf [{}] already registered previously !");
      throw new VoteAlreadyRegisteredException("Vote already computed on session!");
    }
    Vote voteNew = new Vote();
    voteNew.setSession(session);
    voteNew.setCpfNumber(numberCpf);
    voteNew.setOptionVotation(choice);
    return voteRepository.save(voteNew);
  }
  
  public List<Result> countBySession(Session session) {
    Objects.requireNonNull(session);
    log.debug("Calculating votes on Session id [{}]", session.getId());
    return voteRepository.countBySession(session);
  }
}

