package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.helper.HelperTest;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceIT {
  
  @Mock
  private TopicService topicService;
  @Mock
  private ManagerSessionService managerSessionService;
  @Mock
  private SessionRepository sessionRepository;
  @InjectMocks
  private SessionService sessionService;
  
  @Nested
  class GetSession {
    
    @Test
    @DisplayName("Find Session by id")
    void testShouldFindByIdSuccessfully() {
      Session expected = HelperTest.createSession(1L, 1L);
      given(sessionRepository.findById(1L)).willReturn(Optional.of(expected));
      
      Session actual = sessionService.findById(1L).get();
      
      assertThat(actual).isNotNull();
      assertThat(actual.getId()).isEqualTo(expected.getId());
      then(sessionRepository).should().findById(1L);
    }
    
    @Test
    @DisplayName("Find all Session")
    void testShouldFindAllSuccessfully() {
      //given
      List<Session> results = new ArrayList<>(3);
      results.add(HelperTest.createSession(1L, 1L));
      results.add(HelperTest.createSession(2L, 2L));
      results.add(HelperTest.createSession(3L, 3L));
      given(sessionRepository.findAll()).willReturn(results);
      
      //when
      List<Session> actual = sessionService.findAll();
      
      //then
      assertThat(actual).isNotNull().isNotEmpty();
      assertThat(actual).extracting(Session::getStatusSession).containsOnly(StatusSession.ABERTO);
      then(sessionRepository).should().findAll();
    }
  }
  
  @Nested
  class ActionsSession {
    
    @Test
    @DisplayName("Session open successfully")
    void testShouldOpenSuccessfully()
        throws TopicWithExistingSessionException, DataNotFoundException {
      //given
      Topic topic = HelperTest.createTopic(1L, "Title #1", "Description #1");
      Session session = HelperTest.createSession(1L, 1L);
      
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
    void testShouldntOpenWithTopicIdNotFound() throws TopicWithExistingSessionException {
      //given
      given(topicService.findById(eq(1L))).willReturn(Optional.empty());
      
      //when
      try {
        sessionService.open(1L, 1L);
        fail("Should throw exception DataNotFoundException");
      } catch (DataNotFoundException e) {
      }
      
      //then
      then(managerSessionService)
          .should(never())
          .doOpen(any(Topic.class), anyLong());
    }
    
    @Test
    @DisplayName("Vote saved successfully")
    void testShouldVoteSuccessfully() throws Exception {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      given(sessionRepository.findById(anyLong())).willReturn(Optional.of(session));
      willDoNothing().given(managerSessionService)
          .onVote(session, "12345678901", OptionVotation.SIM);
      
      //when
      sessionService.vote(session.getId(), "12345678901", OptionVotation.SIM);
      
      //then
      verify(managerSessionService).onVote(session, "12345678901", OptionVotation.SIM);
    }
    
    @Test
    @DisplayName("Can't vote with session id not found")
    void testShouldntVoteWithSessionIdNotFound()
        throws Exception {
      //given
      given(sessionService.findById(anyLong())).willReturn(Optional.empty());
      
      //when
      try {
        sessionService.vote(1L, "12345678901", OptionVotation.SIM);
      } catch (DataNotFoundException e) {
      }
      
      //then
      then(managerSessionService).should(never())
          .onVote(any(Session.class), anyString(), any(OptionVotation.class));
    }
  }
}
