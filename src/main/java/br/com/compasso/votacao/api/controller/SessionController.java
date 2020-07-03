package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.SessionRequest;
import br.com.compasso.votacao.api.adapter.SessionResponse;
import br.com.compasso.votacao.api.adapter.VoteRequest;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.mapper.SessionMapper;
import br.com.compasso.votacao.api.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sessão", description = "Sessão de votação da Pauta")
@Slf4j
@RestController
@RequestMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class SessionController {
  
  @Autowired
  private SessionService sessionService;
  @Autowired
  private SessionMapper sessionMapper;
  
  @Operation(summary = "Lista Sessões", description = "Lista todas as Sessões de votação cadastradas")
  @ApiResponse(description = "Lista com informações respectivas de cada Sessão", content = @Content(mediaType = "application/json",
      array = @ArraySchema(schema = @Schema(implementation = SessionResponse.class))))
  @GetMapping
  public List<SessionResponse> findAll() {
    return sessionMapper.from(sessionService.findAll());
  }
  
  @Operation(summary = "Localiza Sessão", description = "Localiza Sessão pelo código de identificação",
      responses = {
          @ApiResponse(description = "Informações da respectiva Sessão", content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = SessionResponse.class))),
          @ApiResponse(responseCode = "404", description = "Sessão não encontrada")})
  @GetMapping("/{id}")
  public SessionResponse findById(
      @PathVariable("id") @Positive(message = "id is invalid") Long id) {
    log.debug("Receiving request with id [{}]", id);
    return sessionMapper.from(sessionService.findById(id)
        .orElseThrow(
            () -> new DataNotFoundException("Session not found on " + id)));
  }
  
  @Operation(summary = "Cria Sessão", description = "Cria um Sessão de votação da Pauta",
      responses = {
          @ApiResponse(description = "Informações da Sessão criada", content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = SessionResponse.class))),
          @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
          @ApiResponse(responseCode = "409", description = "Pauta com Sessão já registrada")
      })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public SessionResponse create(@Valid @RequestBody SessionRequest sessionRequest) {
    log.debug("Receiving request with data [{}]", sessionRequest);
    return sessionMapper
        .from(
            sessionService.open(sessionRequest.getTopicId(), sessionRequest.getMinuteTimeVoting()));
  }
  
  @Operation(summary = "Vota", description = "Registra Voto na Sessão")
  @ApiResponses({
      @ApiResponse(responseCode = "400", description = "Número do CPF é invalido"),
      @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
      @ApiResponse(responseCode = "409", description = "Associado não apto para votar"),
      @ApiResponse(responseCode = "409 ", description = "Tempo de votação da Sessão expirado"),
      @ApiResponse(responseCode = "409  ", description = "Sessão não aberta para votação"),
      @ApiResponse(responseCode = "503", description = "Serviço de liberação do voto indisponível no momento")
  })
  @PostMapping(path = "/{id}/votes", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void vote(@PathVariable("id") @Positive(message = "id is invalid") Long id,
      @Valid @RequestBody VoteRequest voteRequest) {
    log.debug("Receiving request with id [{}] and data [{}]", id, voteRequest);
    sessionService
        .vote(id,
            voteRequest.getCpf(),
            voteRequest.getVote());
  }
}
