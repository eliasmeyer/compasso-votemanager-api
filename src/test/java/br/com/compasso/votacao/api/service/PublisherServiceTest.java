package br.com.compasso.votacao.api.service;

import static br.com.compasso.votacao.api.helper.TestHelper.makeListTopics;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import br.com.compasso.votacao.api.mapper.TopicMapper;
import br.com.compasso.votacao.api.mapper.TopicMapperImpl;
import br.com.compasso.votacao.api.message.ResultPublisher;
import br.com.compasso.votacao.api.model.Topic;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {
  
  @Mock
  private ResultPublisher resultPublisher;
  @Mock
  private TopicMapper topicMapper;
  @InjectMocks
  private PublisherService publisherService;
  private final TopicMapperImpl topicMapperImpl = new TopicMapperImpl();
  
  @Nested
  class ActionPublisher {
    
    @Test
    @DisplayName("Publish all messages")
    void testShouldPublishAllSuccessfully() {
      //given
      List<Topic> topicList = makeListTopics(3);
      MessageChannel mockMessageChannel = mock(MessageChannel.class);
      given(resultPublisher.output()).willReturn(mockMessageChannel);
      given(mockMessageChannel.send(any(Message.class))).willReturn(Boolean.TRUE);
      given(topicMapper.from(topicList)).willAnswer(invocation -> topicMapperImpl.from(topicList));
      
      //when
      publisherService.publishAll(topicList);
      
      //then
      then(topicMapper).should(times(1)).from(topicList);
      then(resultPublisher.output()).should(times(3)).send(any(Message.class));
    }
  }
}
