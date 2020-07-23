package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.helper.TestHelper;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessSessionServiceTest {
  
  @Mock
  private ResultService resultService;
  @Mock
  private TopicService topicService;
  @InjectMocks
  private ProcessSessionService processSessionService;
  
  @Nested
  class ActionsResult {
    
    @Test
    void testShouldComputeSuccessfully() {
      //given
      Session session = TestHelper.createSession(1L, 1L);
      session.setStatusSession(StatusSession.APURANDO);
      Topic topic = TestHelper.createTopic("Test #123", "Description 123");
      Result result = TestHelper.createResult(1L, OptionVotation.NAO, 20L);
      
      given(resultService.calculate(any(Session.class))).willReturn(result);
      given(topicService.findById(anyLong())).willReturn(Optional.of(topic));
      
      //when
      Session actual = processSessionService.compute(session);
      
      //then
      assertThat(actual.getStatusSession()).as("Check that statusSession is set as CLOSED")
          .isEqualTo(StatusSession.FECHADO);
      then(resultService).should(times(1)).calculate(any(Session.class));
      then(topicService).should(times(1)).findById(1L);
    }
    
    @Test
    void testShouldNotComputeSuccessfully() {
      //given
      Session session = TestHelper.createSession(1L, 1L);
      session.setStatusSession(StatusSession.APURANDO);
      willThrow(new DataNotFoundException()).given(topicService).findById(1L);
      
      //when
      Session actual = processSessionService.compute(session);
      
      //then
      assertThat(actual.getStatusSession()).as("Check that statusSession is set as OPEN")
          .isEqualTo(StatusSession.ABERTO);
    }
  }
}
