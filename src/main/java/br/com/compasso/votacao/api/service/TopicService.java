package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TitleAlreadyRegisteredException;
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
  
  public Topic save(String title, String description) {
    Objects.requireNonNull(title);
  
    Optional<Topic> topicExist = topicRepository.findByTitleIgnoreCase(title);
    if (topicExist.isPresent()) {
      throw new TitleAlreadyRegisteredException("Topic with title already registered");
    }
  
    Topic topic = new Topic();
    topic.setTitle(title);
    topic.setDescription(description);
    return topicRepository.save(topic);
  }
  
  public Topic update(Long topicId, String title, String description) throws DataNotFoundException {
    Objects.requireNonNull(topicId);
    Objects.requireNonNull(title);
    
    Topic update = topicRepository
        .findById(topicId)
        .orElseThrow(() -> new DataNotFoundException("Topic not found on " + topicId));
    
    update.setTitle(title);
    update.setDescription(description);
    return topicRepository.save(update);
  }
  
  @Transactional(readOnly = true)
  public List<Topic> findAll() {
    return topicRepository.findAll();
  }
  
  @Transactional(readOnly = true)
  public Optional<Topic> findById(Long id) {
    Objects.requireNonNull(id);
    return topicRepository.findById(id);
  }
  
  public void delete(Long id) throws DataNotFoundException {
    Objects.requireNonNull(id);
    Topic topic = topicRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Topic not found on " + id));
    topicRepository.delete(topic);
  }
  
}
