package br.com.compasso.votacao.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.helper.HelperTest;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.TopicRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultServiceIT {
  
  @Mock
  private TopicRepository topicRepository;
  @InjectMocks
  private ResultService resultService;
  
  @Nested
  class ActionsResult {
    
    @Test
    @DisplayName("Result saved successfully")
    void testShouldSaveSuccessfully() {
      //given
      List<Result> expectedResults = new ArrayList<>(1);
      expectedResults.add(HelperTest.createResult(1L, OptionVotation.NAO, 10L));
      Topic expectedTopic = HelperTest.createTopic(1L, "Topic #1", "Description #1");
      given(topicRepository.save(any(Topic.class)))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      
      //when
      Result actual = resultService.save(expectedTopic, expectedResults);
      
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getElectedOption()).isEqualTo(OptionVotation.NAO);
      assertThat(actual.getTotalVotes()).isEqualTo(10L);
      then(topicRepository).should().save(any(Topic.class));
    }
  }
}
