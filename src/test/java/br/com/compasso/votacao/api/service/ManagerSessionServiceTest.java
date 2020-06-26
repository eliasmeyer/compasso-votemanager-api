package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.helper.HelperTest;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
import br.com.compasso.votacao.api.service.verifier.VerifierOnSession;
import java.time.LocalDateTime;
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
class ManagerSessionServiceTest {
  
  @Mock
  private VoteService voteService;
  @Mock
  private ResultService resultService;
  @Mock
  private VerifierOnSession verifierOnSession;
  @Mock
  private SessionRepository sessionRepository;
  @InjectMocks
  private ManagerSessionService managerSessionService;
  
  @Nested
  class ActionsManagerSession {
    
    @Test
    @DisplayName("Open new session successfully")
    void testShouldOpenSuccessfully() throws TopicWithExistingSessionException {
      //given
      Topic topic = HelperTest.createTopic(1L, "Title 1#", "Description #1");
      
      given(sessionRepository.findById(anyLong())).willReturn(Optional.empty());
      given(sessionRepository.save(any(Session.class)))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      
      //when
      Session actual = managerSessionService.doOpen(topic, 1L);
      
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getStatusSession()).isEqualTo(StatusSession.ABERTO);
      assertThat(actual.getDateTimeClosing()).isNotNull();
      assertThat(actual.getDateTimeOpening()).isNotNull();
      assertThat(actual.getTopic()).isNotNull();
      then(sessionRepository).should(times(1)).save(any(Session.class));
    }
    
    @Test
    @DisplayName("Can't open with session already registered")
    void testShouldntOpenSessionAlreadyRegistered() throws DataNotFoundException {
      //given
      Topic topic = HelperTest.createTopic(1L, "Title 1#", "Description #1");
      given(sessionRepository.findById(1L)).willReturn(Optional.of(new Session()));
  
      //when
      assertThatExceptionOfType(TopicWithExistingSessionException.class)
          .isThrownBy(() -> {
            managerSessionService.doOpen(topic, 1L);
          });
  
      //then
      then(sessionRepository)
          .should(never())
          .save(any(Session.class));
    }
    
    @Test
    @DisplayName("Vote save successfully")
    void testShouldVoteSuccessfully() throws Exception {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      willDoNothing().given(verifierOnSession).validate(any(Session.class), anyString());
      
      //when
      managerSessionService.onVote(session, "12345678901", OptionVotation.SIM);
      
      //then
      verify(voteService, times(1))
          .register(any(Session.class), anyString(), eq(OptionVotation.SIM));
    }
    
    @Test
    @DisplayName("Can't vote with time session expired")
    void testShouldOnVoteWithStatusIsntOpen() throws Exception {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      session.setStatusSession(StatusSession.FECHADO);
  
      willThrow(VotingTimeSessionExpiredException.class).given(verifierOnSession)
          .validate(any(Session.class), anyString());
  
      //when      
      assertThatExceptionOfType(VotingTimeSessionExpiredException.class)
          .isThrownBy(() -> {
            managerSessionService.onVote(session, "12345678901", OptionVotation.SIM);
          });
  
      //then
      then(voteService)
          .should(never())
          .register(any(Session.class), anyString(), any(OptionVotation.class));
    }
    
    @Test
    @DisplayName("Compute votes and close session successfully")
    void testShouldDoCloseSuccessfully() {
      //given
      //Associate associate = HelperTest.createAssociate(1L, "Associate #1");
      Topic topic = HelperTest.createTopic(1L, "Title 1#", "Description #1");
      Session session = HelperTest.createSession(1L, 1L);
      session.setTopic(topic);
      List<Session> sessions = new ArrayList<>(1);
      sessions.add(session);
      Result result = HelperTest.createResult(1L, OptionVotation.NAO, 10L);
      List<Result> results = new ArrayList<>(1);
      results.add(result);
      given(sessionRepository
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(any(LocalDateTime.class)))
          .willReturn(sessions);
      given(sessionRepository
          .saveAll(sessions))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      given(voteService.countBySession(session)).willReturn(results);
      given(resultService.save(session.getTopic(), results))
          .willReturn(result);
      willDoNothing().given(sessionRepository).flush();
      
      //when
      managerSessionService.onClose();
      
      //then
      assertThat(sessions).flatExtracting(Session::getStatusSession)
          .contains(StatusSession.FECHADO);
      then(sessionRepository)
          .should(times(2))
          .saveAll(sessions);
      then(sessionRepository)
          .should(times(1))
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(any(LocalDateTime.class));
      then(sessionRepository)
          .should(times(1))
          .flush();
      then(voteService)
          .should(times(1))
          .countBySession(session);
      then(resultService)
          .should(times(1))
          .save(any(Topic.class), eq(results));
    }
  }
}
