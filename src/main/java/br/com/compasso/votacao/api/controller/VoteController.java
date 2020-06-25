package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.VoteResponse;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.mapper.VoteMapper;
import br.com.compasso.votacao.api.service.VoteService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class VoteController {
  
  @Autowired
  private VoteService voteService;
  @Autowired
  private VoteMapper voteMapper;
  
  @GetMapping("/sessions/{id}/votes")
  public List<VoteResponse> findAllBySessionId(@PathVariable("id") Long sessionId)
      throws DataNotFoundException {
    return voteMapper.from(voteService.findAllBySessionId(sessionId));
  }
  
  @GetMapping("/sessions/{id}/votes/associates")
  public VoteResponse findBySessionIdAndAssociateId(
      @PathVariable("id") Long sessionId,
      @RequestParam(name = "cpf") String cpf) throws DataNotFoundException {
    return voteMapper.from(voteService.findBySessionIdAndCpfNumber(sessionId, cpf));
  }
  
}
