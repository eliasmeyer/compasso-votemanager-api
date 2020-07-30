package br.com.compasso.votacao.api.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.helper.TestHelper;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
  
  @Mock
  private VoteService voteService;
  @InjectMocks
  private ResultService resultService;
  
  @Nested
  class ActionsResult {
    
    @Test
    @DisplayName("Result calculated successfully")
    void testShouldCalculateSuccessfully() {
      //given
      Session session = TestHelper.createSession(1L, 1L);
      List<Result> expectedResults = TestHelper.createResultList();
      given(voteService.countBySession(any(Session.class))).willReturn(expectedResults);
      
      //when
      Result result = resultService.calculate(session);
      
      //then
      assertThat(result.getElectedOption()).as("Check that electedOption is set").isNotNull();
      assertThat(result.getTotalVotes()).as("Check that totalVote is set").isNotNull();
    }
    
    @Test
    @DisplayName("Result calculated as INDETERMINADO")
    void testShouldCalculateAsINDETERMINADO() {
      //given
      Session session = TestHelper.createSession(1L, 1L);
      given(voteService.countBySession(any(Session.class))).willReturn(Collections.emptyList());
      
      //when
      Result result = resultService.calculate(session);
      
      //then
      assertThat(result.getElectedOption()).as("Check that electedOption is set").isNotNull();
      assertThat(result.getElectedOption()).as("Check that electedOption is set").isEqualTo(
          OptionVotation.INDETERMINADO);
      assertThat(result.getTotalVotes()).as("Check that totalVote is set").isNotNull();
    }
  }
}
