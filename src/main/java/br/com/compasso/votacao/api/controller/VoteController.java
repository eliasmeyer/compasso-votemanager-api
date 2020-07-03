package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.VoteResponse;
import br.com.compasso.votacao.api.mapper.VoteMapper;
import br.com.compasso.votacao.api.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Voto", description = "Voto da Sessão")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class VoteController {
  
  @Autowired
  private VoteService voteService;
  @Autowired
  private VoteMapper voteMapper;
  
  @Operation(summary = "Lista Votos", description = "Lista todas os Votos de determinada Sessão")
  @ApiResponse(description = "Lista com informações respectivas de cada Voto",
      content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoteResponse.class))))
  @GetMapping("/sessions/{id}/votes")
  public List<VoteResponse> findAllBySessionId(
      @PathVariable("id") @Positive(message = "id is invalid") Long sessionId) {
    log.debug("Receiving request with id [{}]", sessionId);
    return voteMapper.from(voteService.findAllBySessionId(sessionId));
  }
  
  @Operation(summary = "Voto do Associado", description = "Voto do Associado de acordo com código da Sessão",
      responses = {
          @ApiResponse(description = "Lista com informações respectivas de cada Voto",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteResponse.class))),
          @ApiResponse(responseCode = "404", description = "Sessão não encontrado"),
          @ApiResponse(responseCode = "404", description = "Voto não encontrado")
      })
  @GetMapping("/sessions/{id}/votes/associates")
  public VoteResponse findBySessionIdAndAssociate(
      @PathVariable("id") @Positive(message = "id is invalid") Long sessionId,
      @RequestParam(name = "cpf") @NotNull @Pattern(regexp = "[0-9]{11}", message = "RequestParam 'cpf' is wrong") String cpf) {
    log.debug("Receiving request with sessionId [{}] and cpf [{}]", sessionId, cpf);
    return voteMapper.from(voteService.findBySessionIdAndCpfNumber(sessionId, cpf));
  }
  
}
