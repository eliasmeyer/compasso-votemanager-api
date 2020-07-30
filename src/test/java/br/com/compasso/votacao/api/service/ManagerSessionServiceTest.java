package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.helper.TestHelper;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.SessionRepository;
import br.com.compasso.votacao.api.service.verifier.VerifierOnSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ManagerSessionServiceTest {
  
  @Mock
  private VoteService voteService;
  @Mock
  private ProcessSessionService processSessionService;
  @Mock
  private PublisherService publisherService;
  @Mock
  private VerifierOnSession verifierOnSession;
  @Mock
  private SessionRepository sessionRepository;
  @InjectMocks
  private ManagerSessionService managerSessionService;
  
  @Nested
  class GetSession {
    
    @Test
    @DisplayName("Find Session by id")
    void testShouldFindByIdSuccessfully() {
      Session expected = TestHelper.createSession(1L, 1L);
      given(sessionRepository.findById(1L)).willReturn(Optional.of(expected));
      
      Session actual = managerSessionService.findById(1L).get();
      
      assertThat(actual).isNotNull();
      assertThat(actual.getId()).isEqualTo(expected.getId());
      then(sessionRepository).should().findById(1L);
    }
    
    @Test
    @DisplayName("Find all Session")
    void testShouldFindAllSuccessfully() {
      //given
      List<Session> results = new ArrayList<>(3);
      results.add(TestHelper.createSession(1L, 1L));
      results.add(TestHelper.createSession(2L, 2L));
      results.add(TestHelper.createSession(3L, 3L));
      given(sessionRepository.findAll()).willReturn(results);
      
      //when
      List<Session> actual = managerSessionService.findAll();
      
      //then
      assertThat(actual).isNotNull().isNotEmpty();
      assertThat(actual).extracting(Session::getStatusSession).containsOnly(StatusSession.ABERTO);
      then(sessionRepository).should().findAll();
    }
  }
  
  @Nested
  class ActionsManagerSession {
    
    @Test
    @DisplayName("Open new session successfully")
    void testShouldOpenSuccessfully() throws TopicWithExistingSessionException {
      //given
      Topic topic = TestHelper.createTopic(1L, "Title 1#", "Description #1");
      
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
    @DisplayName("Vote save successfully")
    void testShouldVoteSuccessfully() throws Exception {
      //given
      Session session = TestHelper.createSession(1L, 1L);
      willDoNothing().given(verifierOnSession).validate(any(Session.class), anyString());
      
      //when
      managerSessionService.onVote(session, "12345678901", OptionVotation.SIM);
      
      //then
      verify(voteService, times(1))
          .register(any(Session.class), anyString(), eq(OptionVotation.SIM));
    }
    
    @Test
    @DisplayName("Can't vote with time session expired")
    void testShouldOnVoteWithStatusIsNotOpen() throws Exception {
      //given
      Session session = TestHelper.createSession(1L, 1L);
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
      ReflectionTestUtils.setField(managerSessionService, "chunkSize", 5);
      List<Session> sliceSessionPageOne = TestHelper.makeListSessions(5, StatusSession.ABERTO);
      List<Session> sliceSessionPageTwo = TestHelper.makeListSessions(4, StatusSession.ABERTO);
  
      List<Session> sliceSessionTotalComputed = TestHelper
          .makeListSessions(9, StatusSession.FECHADO);
  
      PageRequest pageRequestOne = PageRequest.of(0, 5);
      PageRequest pageRequestTwo = PageRequest.of(1, 5);
      PageRequest pageRequestThree = PageRequest.of(2, 5);
  
      Slice<Session> sliceSessionOne = new SliceImpl<>(sliceSessionPageOne, pageRequestOne, true);
      Slice<Session> sliceSessionTwo = new SliceImpl<>(sliceSessionPageTwo, pageRequestTwo, false);
      Slice<Session> sliceSessionThree = new SliceImpl<>(Collections.emptyList(), pageRequestThree,
          false);
  
      // (TOTAL / CHUNK SIZE)
      int expectedTime = 10 / 5;
  
      given(sessionRepository
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(any(LocalDateTime.class),
              eq(pageRequestOne)))
          .willReturn(sliceSessionOne);
  
      given(sessionRepository
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(any(LocalDateTime.class),
              eq(pageRequestTwo)))
          .willReturn(sliceSessionTwo);
  
      given(sessionRepository
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(any(LocalDateTime.class),
              eq(pageRequestThree)))
          .willReturn(sliceSessionThree);
  
      given(sessionRepository
          .saveAll(anyList()))
          .willReturn(sliceSessionOne.getContent());
  
      given(processSessionService.compute(any(Session.class)))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
  
      willDoNothing().given(sessionRepository).flush();
  
      //when
      managerSessionService.onClose();
  
      //then      
      then(sessionRepository)
          .should(times(expectedTime + 1))
          .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(any(LocalDateTime.class), any(
              Pageable.class));
      then(sessionRepository)
          .should(times(expectedTime * 2))
          .saveAll(anyList());
      then(sessionRepository)
          .should(times(expectedTime * 2))
          .flush();
      then(publisherService)
          .should(times(expectedTime))
          .publishAll(anyList());
    }
  }
}
