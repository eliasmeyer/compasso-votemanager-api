package br.com.compasso.votacao.api.service;

import br.com.compasso.votacao.api.adapter.TopicResponse;
import br.com.compasso.votacao.api.mapper.TopicMapper;
import br.com.compasso.votacao.api.message.ResultPublisher;
import br.com.compasso.votacao.api.model.Topic;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PublisherService {
  
  @Autowired
  private ResultPublisher resultPublisher;
  @Autowired
  private TopicMapper mapper;
  
  @Transactional(readOnly = true)
  public void publishAll(List<Topic> topics) {
    List<TopicResponse> topicResponseList = mapper.from(topics);
    for (TopicResponse response : topicResponseList) {
      try {
        resultPublisher.output().send(MessageBuilder.withPayload(response).build());
      } catch (Exception ex) {
        log.error("ERROR ON PUBLISHING TOPIC RESULT [{}].", response.getId(), ex);
      }
    }
  }
}
