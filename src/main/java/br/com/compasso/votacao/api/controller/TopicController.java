package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.TopicRequest;
import br.com.compasso.votacao.api.adapter.TopicResponse;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.mapper.TopicMapper;
import br.com.compasso.votacao.api.service.TopicService;
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

@Slf4j
@RestController
@RequestMapping(value = "/topics",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TopicController {
  
  @Autowired
  private TopicService topicService;
  @Autowired
  private TopicMapper topicMapper;
  
  @GetMapping
  public List<TopicResponse> findAll() {
    return topicMapper.from(topicService.findAll());
  }
  
  @GetMapping("/{id}")
  public TopicResponse findById(@PathVariable("id") @Positive(message = "id is invalid") Long id) {
    log.debug("Receiving request with id [{}]", id);
    return topicMapper.from(topicService.findById(id)
        .orElseThrow(() -> new DataNotFoundException("Topic not found on " + id)));
  }
  
  @PostMapping
  public TopicResponse create(@Valid @RequestBody TopicRequest topicRequest) {
    log.debug("Receiving request with data", topicRequest);
    return topicMapper
        .from(topicService.save(topicRequest.getTitle(), topicRequest.getDescription()));
  }
  
  @PutMapping("/{id}")
  public TopicResponse update(@PathVariable("id") @Positive(message = "id is invalid") Long id,
      @Valid @RequestBody TopicRequest topicRequest) {
    log.debug("Receiving request with id [{}] and data [{}]", id, topicRequest);
    return topicMapper
        .from(topicService.update(id, topicRequest.getTitle(), topicRequest.getDescription()));
  }
  
  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") @Positive(message = "id is invalid") Long id) {
    log.debug("Receiving request with id [{}]", id);
    topicService.delete(id);
  }
}
