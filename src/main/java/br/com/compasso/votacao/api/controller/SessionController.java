package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.SessionRequest;
import br.com.compasso.votacao.api.adapter.SessionResponse;
import br.com.compasso.votacao.api.adapter.VoteRequest;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.mapper.SessionMapper;
import br.com.compasso.votacao.api.service.SessionService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/sessions",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class SessionController {
  
  @Autowired
  private SessionService sessionService;
  @Autowired
  private SessionMapper sessionMapper;
  
  @GetMapping
  public List<SessionResponse> findAll() {
    return sessionMapper.from(sessionService.findAll());
  }
  
  @GetMapping("/{id}")
  public SessionResponse findById(@PathVariable("id") Long id) throws DataNotFoundException {
    return sessionMapper.from(sessionService.findById(id)
        .orElseThrow(
            () -> new DataNotFoundException("Session not found on " + id)));
  }
  
  @GetMapping("/status")
  public List<SessionResponse> findByStatus(
      @RequestParam(name = "status") StatusSession statusSession) {
    return sessionMapper.from(sessionService.findAllByStatusSession(statusSession));
  }
  
  @PostMapping
  public SessionResponse create(@Valid @RequestBody SessionRequest sessionRequest)
      throws TopicWithExistingSessionException, DataNotFoundException {
    return sessionMapper
        .from(
            sessionService.open(sessionRequest.getTopicId(), sessionRequest.getMinuteTimeVoting()));
  }
  
  @PostMapping("/{id}/votes")
  public void vote(@PathVariable("id") Long id, @Valid @RequestBody VoteRequest voteRequest)
      throws Exception {
    sessionService
        .vote(id,
            voteRequest.getCpf(),
            voteRequest.getVote());
  }
}
