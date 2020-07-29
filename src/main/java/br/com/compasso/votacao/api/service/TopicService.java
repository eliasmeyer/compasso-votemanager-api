package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TitleAlreadyRegisteredException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.TopicRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class TopicService {
  
  @Autowired
  private TopicRepository topicRepository;
  @Autowired
  private SessionService sessionService;
  
  public Topic save(String title, String description) {
    Objects.requireNonNull(title);
    
    Optional<Topic> topicExist = topicRepository.findByTitleIgnoreCase(title);
    if (topicExist.isPresent()) {
      log.error("Topic with title [{}] already registered", title);
      throw new TitleAlreadyRegisteredException("Topic with title already registered");
    }
    
    Topic topic = new Topic();
    topic.setTitle(title);
    topic.setDescription(description);
    topic = topicRepository.save(topic);
    log.info("Topic with title [{}] created successfully", title);
    return topic;
  }
  
  public Topic update(Long topicId, String title, String description) throws DataNotFoundException {
    Objects.requireNonNull(topicId);
    Objects.requireNonNull(title);
  
    Topic update = topicRepository
        .findById(topicId)
        .orElseThrow(() -> {
          log.error("Topic [{}] not found", topicId);
          return new DataNotFoundException("Topic not found on " + topicId);
        });
  
    update.setTitle(title);
    update.setDescription(description);
    update = topicRepository.save(update);
    log.info("Topic id [{}] updated successfully", topicId);
    return update;
  }
  
  @Transactional(readOnly = true)
  public List<Topic> findAll() {
    log.debug("Find All");
    return topicRepository.findAll();
  }
  
  @Transactional(readOnly = true)
  public Optional<Topic> findOneWithResultById(Long id) {
    Objects.requireNonNull(id);
    log.debug("Find Topic by id [{}]", id);
    return topicRepository.findOneWithResultById(id);
  }
  
  @Transactional(readOnly = true)
  public Optional<Topic> findById(Long id) {
    Objects.requireNonNull(id);
    log.debug("Find Topic by id [{}]", id);
    return topicRepository.findById(id);
  }
  
  public void delete(Long id) throws DataNotFoundException {
    Objects.requireNonNull(id);
    log.debug("Delete Topic by id [{}]", id);
    Topic topic = topicRepository
        .findById(id)
        .orElseThrow(() -> {
          log.error("Topic id [{}] not found", id);
          return new DataNotFoundException("Topic not found on " + id);
        });
    
    //Check if session is registered 
    Optional<Session> session = sessionService.findById(id);
    if (session.isPresent()) {
      log.error("Topic id [{}] with existing session", id);
      throw new TopicWithExistingSessionException("Topic with Session already registered");
    }
    
    topicRepository.delete(topic);
    log.info("Topic id [{}] deleted successfully", id);
  }
  
}
