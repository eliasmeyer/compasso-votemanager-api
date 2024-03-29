package br.com.compasso.votacao.api.service;

import static br.com.compasso.votacao.api.helper.TestHelper.createSession;
import static br.com.compasso.votacao.api.helper.TestHelper.createTopic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TitleAlreadyRegisteredException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.repository.TopicRepository;
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
class TopicServiceTest {
  
  @Mock
  private TopicRepository topicRepository;
  @Mock
  private SessionService sessionService;
  @InjectMocks
  private TopicService topicService;
  
  @Nested
  class GetTopic {
    
    @Test
    @DisplayName("Find Topic by id")
    void testShouldFindByIdSuccessfully() {
      //given
      Topic expected = createTopic(1L, "Title #1", "Description #1");
      given(topicRepository.findById(1L)).willReturn(Optional.of(expected));
      
      //when
      final Topic actual = topicService.findById(1L).get();
      
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
      assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
      then(topicRepository).should().findById(1L);
    }
    
    @Test
    @DisplayName("Return Topics's list ")
    void testShouldFindAllSuccessfully() {
      //given
      List<Topic> expected = new ArrayList<>(3);
      expected.add(createTopic(1L, "Topic #1", "Description #1"));
      expected.add(createTopic(2L, "Topic #2", "Description #2"));
      expected.add(createTopic(3L, "Topic #3", "Description #3"));
  
      given(topicRepository.findAll()).willReturn(expected);
  
      //when
      List<Topic> actual = topicService.findAll();
  
      //then
      assertThat(actual).isNotNull().isNotEmpty();
      assertThat(actual).extracting(Topic::getTitle)
          .contains("Topic #1", "Topic #2", "Topic #3");
      then(topicRepository).should().findAll();
    }
  }
  
  @Nested
  class ActionsTopics {
    
    @Test
    @DisplayName("Topic saved successfully")
    void testShouldSaveSuccessfully() {
      given(topicRepository.save(any(Topic.class)))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
  
      //when
      Topic actual = topicService.save("Title #2", "Description #2");
  
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getTitle()).isEqualTo("Title #2");
      assertThat(actual.getDescription()).isEqualTo("Description #2");
      then(topicRepository).should().save(any(Topic.class));
    }
  
    @Test
    @DisplayName("Topic not save with title equals")
    void testShouldntSaveWithTitleEquals() {
      given(topicRepository.findByTitleIgnoreCase("Title Equals #1"))
          .willReturn(Optional.of(createTopic(1L, "Title Equals #1", "Description Equals #1")));
    
      assertThatExceptionOfType(TitleAlreadyRegisteredException.class)
          .isThrownBy(() -> {
            topicService.save("Title Equals #1", "Description Equals #1");
          });
    
      //then
      then(topicRepository).should(never()).save(any(Topic.class));
    }
  
    @Test
    @DisplayName("Update Topic successfully")
    void testShouldUpdateSuccessfully() throws DataNotFoundException {
      Topic expected = createTopic(1L, "Title #1", "Description #1");
      given(topicRepository.findById(anyLong())).willReturn(Optional.ofNullable(expected));
      given(topicRepository.save(any(Topic.class)))
          .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    
      //when
      Topic actual = topicService.update(1L, "Update title #1", "Update description #1");
      
      //then
      assertThat(actual).isNotNull();
      assertThat(actual.getTitle()).isEqualTo("Update title #1");
      assertThat(actual.getDescription()).isEqualTo("Update description #1");
      then(topicRepository).should().findById(anyLong());
      then(topicRepository).should().save(any(Topic.class));
    }
    
    @Test
    @DisplayName("Update Topic with id not found")
    void testUpdateWithIdNotFound() {
      //given
      given(topicRepository.findById(anyLong()))
          .willReturn(Optional.empty());
  
      //when
      assertThatExceptionOfType(DataNotFoundException.class)
          .isThrownBy(() -> {
            topicService.update(1L, "Update title #1", "Update description #1");
          });
  
      //then
      then(topicRepository).should(never()).save(any(Topic.class));
    }
    
    @Test
    @DisplayName("Delete Topic successfully")
    void testShouldDeleteSuccessfully() throws DataNotFoundException {
      //given
      Topic expected = createTopic(1L, "Title #1", "Description #1");
      given(topicRepository.findById(anyLong()))
          .willReturn(Optional.ofNullable(expected));
      given(sessionService.findById(1L))
          .willReturn(Optional.empty());
  
      //when
      topicService.delete(1L);
  
      //then
      then(topicRepository).should().findById(anyLong());
      then(sessionService).should().findById(anyLong());
      then(topicRepository).should().delete(any(Topic.class));
    }
    
    @Test
    @DisplayName("Delete Topic with id not found")
    void testShoudntDeleteWithIdNotFound() {
      //given
      given(topicRepository.findById(anyLong()))
          .willReturn(Optional.empty());
  
      //when
      assertThatExceptionOfType(DataNotFoundException.class)
          .isThrownBy(() -> {
            topicService.delete(1L);
          });
  
      //then
      then(topicRepository).should(never()).delete(any(Topic.class));
    }
  
    @Test
    @DisplayName("Delete Topic with session registered")
    void testShoudntDeleteWithSessionRegistered() {
      //given
      Topic topic = createTopic(1L, "Title #1", "Description #1");
      Session session = createSession(1L, 2L);
      given(topicRepository.findById(anyLong()))
          .willReturn(Optional.of(topic));
      given(sessionService.findById(1L)).willReturn(Optional.of(session));
    
      //when
      assertThatExceptionOfType(TopicWithExistingSessionException.class)
          .isThrownBy(() -> {
            topicService.delete(1L);
          });
    
      //then
      then(topicRepository).should(never()).delete(any(Topic.class));
    }
  }
}
