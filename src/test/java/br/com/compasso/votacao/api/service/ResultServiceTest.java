package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.helper.HelperTest;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Topic;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
  
  @InjectMocks
  private ResultService resultService;
  
  @Nested
  class ActionsResult {
    
    @Test
    @DisplayName("Result successfully computed")
    void testShouldSaveSuccessfully() {
      //given
      List<Result> expectedResults = new ArrayList<>(1);
      expectedResults.add(HelperTest.createResult(1L, OptionVotation.NAO, 10L));
      Topic expectedTopic = HelperTest.createTopic(1L, "Topic #1", "Description #1");
      //when
      Result actual = resultService.computer(expectedTopic, expectedResults);
      
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getElectedOption()).isEqualTo(OptionVotation.NAO);
      assertThat(actual.getTotalVotes()).isEqualTo(10L);
    }
  }
}
