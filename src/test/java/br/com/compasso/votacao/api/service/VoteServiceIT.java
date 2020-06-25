package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.helper.HelperTest;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.repository.VoteRepository;
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
class VoteServiceIT {
  
  @Mock
  private SessionService sessionService;
  @Mock
  private VoteRepository voteRepository;
  @InjectMocks
  private VoteService voteService;
  
  @Nested
  class GetVotes {
    
    @Test
    @DisplayName("Find all by session id")
    void testShouldFindAllBySessionIdSuccessfully() throws DataNotFoundException {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      List<Vote> expectedVotes = new ArrayList<>(1);
      expectedVotes.add(HelperTest.createVote(session, "12345678901", OptionVotation.SIM));
      given(sessionService.findById(anyLong()))
          .willReturn(Optional.of(session));
      given(voteRepository.findAllBySession(any(Session.class)))
          .willReturn(expectedVotes);
      
      //when
      List<Vote> actual = voteService.findAllBySessionId(1L);
      
      //then
      assertThat(actual).isNotNull().isNotEmpty();
      then(sessionService).should(times(1)).findById(1L);
      then(voteRepository).should(times(1)).findAllBySession(session);
    }
    
    @Test
    @DisplayName("Can't find all by session when id not found")
    void testShouldntFindAllBySessionIdNotFound() {
      //given
      given(sessionService.findById(anyLong())).willReturn(Optional.empty());
      
      //when
      try {
        voteService.findAllBySessionId(1L);
        fail("Should throw exception DataNotFoundException");
      } catch (DataNotFoundException e) {
      }
      
      //then
      then(voteRepository).should(never()).findAllBySession(any(Session.class));
    }
    
    @Test
    @DisplayName("Find by session id and Cpf number")
    void testShouldFindBySessionIdAndCpfNumber() throws DataNotFoundException {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      Vote vote = HelperTest.createVote(session, "12345678901", OptionVotation.SIM);
      given(sessionService.findById(1L))
          .willReturn(Optional.of(session));
      given(voteRepository.findBySessionAndCpfNumber(session, "12345678901"))
          .willReturn(Optional.of(vote));
  
      //when
      Vote expected = voteService.findBySessionIdAndCpfNumber(1L, "12345678901");
  
      //then
      assertThat(expected).isNotNull();
      then(voteRepository).should(times(1))
          .findBySessionAndCpfNumber(any(Session.class), anyString());
    }
    
    @Test
    @DisplayName("Can't find vote by session and associate when session id not found")
    void testShouldntFindBySessionIdAndAssociateIdWithIdSessionNotFound() {
      //given
      given(sessionService.findById(anyLong())).willReturn(Optional.empty());
      
      //when
      try {
        voteService.findBySessionIdAndCpfNumber(1L, "12345678901");
      } catch (DataNotFoundException e) {
      }
      
      //then
      then(voteRepository).should(never())
          .findBySessionAndCpfNumber(any(Session.class), anyString());
    }
    
    @Test
    @DisplayName("Computing votes by session")
    void countBySession() {
      //given
      List<Result> expected = new ArrayList<>();
      expected.add(HelperTest.createResult(1L, OptionVotation.NAO, 10L));
      Session session = HelperTest.createSession(1L, 1L);
      given(voteRepository.countBySession(any(Session.class))).willReturn(expected);
      
      //when
      List<Result> actual = voteService.countBySession(session);
      
      //then
      assertThat(actual).isNotNull().isNotEmpty();
      Result result = actual.stream().findFirst().get();
      assertThat(result.getTotalVotes()).isEqualTo(10L);
      assertThat(result.getElectedOption()).isEqualTo(OptionVotation.NAO);
      then(voteRepository).should(times(1)).countBySession(any(Session.class));
    }
  }
  
  @Nested
  class ActionsVote {
    
    @Test
    @DisplayName("Save vote successfully")
    void testShouldSaveSuccessfully() throws VoteAlreadyRegisteredException {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      Vote vote = HelperTest.createVote(session, "12345678901", OptionVotation.SIM);
      given(voteRepository.findBySessionAndCpfNumber(session, "12345678901"))
          .willReturn(Optional.empty());
      
      given(voteRepository.save(any(Vote.class)))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      
      //when
      Vote actual = voteService.register(session, "12345678901", OptionVotation.SIM);
      
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getSession()).isNotNull();
      assertThat(actual.getCpfNumber()).isNotNull();
      assertThat(actual.getOptionVotation()).isEqualTo(OptionVotation.SIM);
      then(voteRepository).should(times(1)).save(any(Vote.class));
    }
    
    @Test
    @DisplayName("Can't register vote")
    void testShouldntRegisterVote() throws VoteAlreadyRegisteredException {
      //given
      Session session = HelperTest.createSession(1L, 1L);
      Vote vote = HelperTest.createVote(session, "12345678901", OptionVotation.SIM);
      given(voteRepository.findBySessionAndCpfNumber(session, "12345678901"))
          .willReturn(Optional.of(vote));
      
      //when
      try {
        Vote actual = voteService.register(session, "12345678901", OptionVotation.SIM);
        fail("Should throw VoteAlreadyRegisteredException");
      } catch (VoteAlreadyRegisteredException e) {
      }
      
      //then
      then(voteRepository).should(never()).save(any(Vote.class));
    }
  }
}
