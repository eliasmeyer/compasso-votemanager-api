package br.com.compasso.votacao.api.services;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exceptions.DataNotFoundException;
import br.com.compasso.votacao.api.exceptions.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.exceptions.VotingTimeExpiredException;
import br.com.compasso.votacao.api.models.Associate;
import br.com.compasso.votacao.api.models.Result;
import br.com.compasso.votacao.api.models.Session;
import br.com.compasso.votacao.api.models.Topic;
import br.com.compasso.votacao.api.models.Vote;
import br.com.compasso.votacao.api.repositories.AssociateRepository;
import br.com.compasso.votacao.api.repositories.TopicRepository;
import br.com.compasso.votacao.api.repositories.VoteRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class VoteManagerService {
  
  @Autowired
  private SessionService sessionService;
  @Autowired
  private ResultService resultService;
  @Autowired
  private TopicRepository topicRepository;
  @Autowired
  private AssociateRepository associateRepository;
  @Autowired
  private VoteRepository voteRepository;
  
  /**
   * Open a voting session
   */
  public Session openSession(Long idTopic, Long optionalVotingTime)
      throws DataNotFoundException {
    log.info("Abrindo sessao de votacao para pauta de codigo {[]}", idTopic);
    Topic topic = topicRepository.findById(idTopic).orElseThrow(() -> new DataNotFoundException());
    return sessionService.create(topic, optionalVotingTime);
  }
  
  /**
   * Selects sessions to count votes and finalize
   */
  public void computeAndClose() {
    log.info("Inicio do processo de fechamento da sessão e contagem de votos");
    sessionService.changeStatusToProcessing();
    List<Session> sessionProcessing = sessionService.getCurrentSessionsToProcess();
    log.debug("Preparando contagem dos votos");
    sessionProcessing
        .parallelStream()
        .forEach(currentSession -> {
          Result result = resultService.compute(currentSession);
          currentSession.getTopic().setResult(result);
          currentSession.setStatusSession(StatusSession.FECHADO);
        });
    log.debug("Encerrando sessoes");
    sessionService.saveAll(sessionProcessing);
  }
  
  /**
   * Register associate's vote
   */
  public Vote vote(Long idAssociate, Long idSession, OptionVotation choiceOfVote)
      throws DataNotFoundException, VoteAlreadyRegisteredException, VotingTimeExpiredException {
    log.info("Registrando voto do associado {[]} na sessao {[]}", idAssociate, idSession);
    Associate elector = associateRepository.findById(idAssociate)
        .orElseThrow(DataNotFoundException::new);
    Session session = sessionService.findById(idSession).orElseThrow(DataNotFoundException::new);
    Optional<Vote> optionalVote = voteRepository.findBySessionAndAssociate(session, elector);
    
    if (optionalVote.isPresent()) {
      log.error("ASSOCIADO JA REGISTROU VOTO NA SESSAO!");
      throw new VoteAlreadyRegisteredException();
    }
    
    Vote vote = new Vote();
    vote.setAssociate(elector);
    vote.setOptionVotation(choiceOfVote);
    vote.setSession(session);
    
    if (sessionService.isOpen(session)) {
      voteRepository.save(vote);
      log.info("Voto registrado");
    } else {
      log.error("TEMPO DE VOTACAO EXPIRADO!");
      throw new VotingTimeExpiredException();
    }
    
    return vote;
  }
}
