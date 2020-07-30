package br.com.compasso.votacao.api.service;

import static br.com.compasso.votacao.api.helper.TestHelper.createSession;
import static br.com.compasso.votacao.api.helper.TestHelper.createTopic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.helper.TestHelper;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
  
  @Mock
  private TopicService topicService;
  @Mock
  private ManagerSessionService managerSessionService;
  @InjectMocks
  private SessionService sessionService;
  
  @Nested
  class GetSession {
    
    @Test
    @DisplayName("Find Session by id")
    void testShouldFindByIdSuccessfully() {
      Session expected = TestHelper.createSession(1L, 1L);
      given(managerSessionService.findById(1L)).willReturn(Optional.of(expected));
  
      Session actual = sessionService.findById(1L).get();
  
      assertThat(actual).isNotNull();
      assertThat(actual.getId()).isEqualTo(expected.getId());
      then(managerSessionService).should().findById(1L);
    }
    
    @Test
    @DisplayName("Find all Session")
    void testShouldFindAllSuccessfully() {
      //given
      List<Session> results = new ArrayList<>(3);
      results.add(TestHelper.createSession(1L, 1L));
      results.add(TestHelper.createSession(2L, 2L));
      results.add(TestHelper.createSession(3L, 3L));
      given(managerSessionService.findAll()).willReturn(results);
  
      //when
      List<Session> actual = sessionService.findAll();
  
      //then
      assertThat(actual).isNotNull().isNotEmpty();
      assertThat(actual).extracting(Session::getStatusSession).containsOnly(StatusSession.ABERTO);
      then(managerSessionService).should().findAll();
    }
  }
  
  @Nested
  class ActionsSession {
  
    @Test
    @DisplayName("Session open successfully")
    void testShouldOpenSuccessfully() {
      //given
      Topic topic = createTopic(1L, "Title #1", "Description #1");
      Session session = TestHelper.createSession(1L, 1L);
    
      given(topicService.findById(1L))
          .willReturn(Optional.of(topic));
    
      given(managerSessionService.doOpen(topic, 1L))
          .willReturn(session);
    
      //when
      Session actual = sessionService.open(1L, 1L);
    
      //then
      assertThat(actual).isNotNull();
      then(topicService).should(times(1)).findById(1L);
      then(managerSessionService).should(times(1)).doOpen(topic, 1L);
    }
  
    @Test
    @DisplayName("Can't open with topic id not found")
    void testShouldNotOpenWithTopicIdNotFound() {
      //given
      given(topicService.findById(eq(1L))).willReturn(Optional.empty());
    
      assertThatExceptionOfType(DataNotFoundException.class)
          .isThrownBy(() -> {
            sessionService.open(1L, 1L);
          });
    
      //then
      then(managerSessionService)
          .should(never())
          .doOpen(any(Topic.class), anyLong());
    }
  
    @Test
    @DisplayName("Can't open with session already registered")
    void testShouldNotOpenSessionAlreadyRegistered() throws DataNotFoundException {
      //given
      Topic topic = createTopic(1L, "Title 1#", "Description #1");
      Session session = createSession(1L, 1L);
      given(topicService.findById(1L)).willReturn(Optional.of(topic));
      given(managerSessionService.findById(1L)).willReturn(Optional.of(session));
    
      //when
      assertThatExceptionOfType(TopicWithExistingSessionException.class)
          .isThrownBy(() -> {
            sessionService.open(1L, 1L);
          });
    
      //then
      then(managerSessionService)
          .should(never())
          .doOpen(any(Topic.class), anyLong());
    }
  
    @Test
    @DisplayName("Vote saved successfully")
    void testShouldVoteSuccessfully() throws Exception {
      //given
      Session session = TestHelper.createSession(1L, 1L);
      given(managerSessionService.findById(anyLong())).willReturn(Optional.of(session));
      willDoNothing().given(managerSessionService)
          .onVote(session, "12345678901", OptionVotation.SIM);
    
      //when
      sessionService.vote(session.getId(), "12345678901", OptionVotation.SIM);
    
      //then
      then(managerSessionService).should(times(1))
          .onVote(session, "12345678901", OptionVotation.SIM);
    }
  
    @Test
    @DisplayName("Can't vote with session id not found")
    void testShouldNotVoteWithSessionIdNotFound() throws Exception {
      //given
      given(sessionService.findById(anyLong())).willReturn(Optional.empty());
    
      //when  
      assertThatExceptionOfType(DataNotFoundException.class)
          .isThrownBy(() -> {
            sessionService.vote(1L, "12345678901", OptionVotation.SIM);
          });
    
      //then
      then(managerSessionService).should(never())
          .onVote(any(Session.class), anyString(), any(OptionVotation.class));
    }
  
    @Test
    @DisplayName("Close session successfully")
    void testShouldCloseSuccessfully() throws ExecutionException, InterruptedException {
      //given
      willDoNothing().given(managerSessionService).onClose();
    
      //when
      sessionService.close();
    
      //then
      then(managerSessionService).should(times(1)).onClose();
    }
  }
}
