package br.com.compasso.votacao.api.controllers;

import br.com.compasso.votacao.api.adapters.SessionRequest;
import br.com.compasso.votacao.api.adapters.SessionResponse;
import br.com.compasso.votacao.api.adapters.VoteRequest;
import br.com.compasso.votacao.api.exceptions.DataNotFoundException;
import br.com.compasso.votacao.api.exceptions.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.exceptions.VotingTimeExpiredException;
import br.com.compasso.votacao.api.mappers.SessionMapper;
import br.com.compasso.votacao.api.models.Session;
import br.com.compasso.votacao.api.models.Vote;
import br.com.compasso.votacao.api.services.SessionService;
import br.com.compasso.votacao.api.services.VoteManagerService;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/sessions")
@RequestMapping("v1")
@Slf4j
public class VoteManagerController {
  
  @Autowired
  private VoteManagerService voteManagerService;
  @Autowired
  private SessionService sessionService;
  @Autowired
  private SessionMapper sessionMapper;
  
  @GetMapping
  public ResponseEntity<List<SessionResponse>> findAll() {
    log.info("Request all Sessions");
    List<Session> sessions = sessionService.findAll();
    List<SessionResponse> sessionResponses = sessionMapper.toSessionResponses(sessions);
    log.info("Returning Sessions");
    return ResponseEntity.ok(sessionResponses);
  }
  
  @PostMapping("/open")
  public ResponseEntity<SessionResponse> open(
      @Valid @RequestBody SessionRequest sessionRequest)
      throws DataNotFoundException {
    
    log.info("Request Open Session with [{}]", sessionRequest);
    
    Session session = voteManagerService
        .openSession(sessionRequest.getIdTopic(), sessionRequest.getMinuteTimeVoting());
    log.debug("Mapping Session {} to Response", session);
    SessionResponse response = sessionMapper.toSessionResponse(session);
    
    log.info("Session opened {}", response);
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("/{id}/votes")
  public ResponseEntity<String> vote(@PathParam("id") Long id,
      @Valid @RequestBody VoteRequest voteRequest)
      throws DataNotFoundException, VoteAlreadyRegisteredException, VotingTimeExpiredException {
    
    log.info("Registering vote for session");
    Vote session = voteManagerService
        .vote(voteRequest.getIdAssociate(), id, voteRequest.getChoiceOfVote());
    log.info("Vote registered successfully.");
    return ResponseEntity.ok("Vote computed successfully");
  }
  
}
