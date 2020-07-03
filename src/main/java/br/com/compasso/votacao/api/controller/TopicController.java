package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.TopicRequest;
import br.com.compasso.votacao.api.adapter.TopicResponse;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.mapper.TopicMapper;
import br.com.compasso.votacao.api.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pauta", description = "Pauta de votação")
@Slf4j
@RestController
@RequestMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TopicController {
  
  @Autowired
  private TopicService topicService;
  @Autowired
  private TopicMapper topicMapper;
  
  @Operation(summary = "Lista Pautas", description = "Lista todas as Pautas cadastradas")
  @ApiResponse(description = "Lista com informações respectivas de cada Pauta",
      content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TopicResponse.class))))
  @GetMapping
  public List<TopicResponse> findAll() {
    return topicMapper.from(topicService.findAll());
  }
  
  @Operation(summary = "Localiza Pauta", description = "Localiza Pauta pelo código de identificação")
  @ApiResponse(description = "Informações da respectiva Pauta", content = @Content(mediaType = "application/json",
      schema = @Schema(implementation = TopicResponse.class)))
  @GetMapping("/{id}")
  public TopicResponse findById(@PathVariable("id") @Positive(message = "id is invalid") Long id) {
    log.debug("Receiving request with id [{}]", id);
    return topicMapper.from(topicService.findById(id)
        .orElseThrow(() -> new DataNotFoundException("Topic not found on " + id)));
  }
  
  @Operation(summary = "Cadastra Pauta", description = "Cadastra uma nova Pauta",
      responses = {
          @ApiResponse(description = "Informações da Pauta cadastrada", content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = TopicResponse.class))),
          @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
          @ApiResponse(responseCode = "409", description = "Pauta com título já cadastrado")})
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public TopicResponse create(@Valid @RequestBody TopicRequest topicRequest) {
    log.debug("Receiving request with data", topicRequest);
    return topicMapper
        .from(topicService.save(topicRequest.getTitle(), topicRequest.getDescription()));
  }
  
  @Operation(summary = "Atualiza Pauta", description = "Atualiza as informações da Pauta existente",
      responses = {
          @ApiResponse(description = "Informações da Pauta atualizada", content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = TopicResponse.class))),
          @ApiResponse(responseCode = "404", description = "Pauta não encontrada")})
  @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public TopicResponse update(@PathVariable("id") @Positive(message = "id is invalid") Long id,
      @Valid @RequestBody TopicRequest topicRequest) {
    log.debug("Receiving request with id [{}] and data [{}]", id, topicRequest);
    return topicMapper
        .from(topicService.update(id, topicRequest.getTitle(), topicRequest.getDescription()));
  }
  
  @Operation(summary = "Deleta Pauta", description = "Deleta Pauta existente",
      responses = {
          @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
          @ApiResponse(responseCode = "409", description = "Pauta com sessão registrada")
      })
  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") @Positive(message = "id is invalid") Long id) {
    log.debug("Receiving request with id [{}]", id);
    topicService.delete(id);
  }
}
