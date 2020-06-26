package br.com.compasso.votacao.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.compasso.votacao.api.adapter.TopicRequest;
import br.com.compasso.votacao.api.config.MapperConfig;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.helper.HelperTest;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TopicController.class)
@Import(MapperConfig.class)
class TopicControllerTest {
  
  @Autowired
  private MockMvc mockMvc;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @MockBean
  private TopicService topicService;
  
  @Test
  void testFindAll() throws Exception {
    List<Topic> topics = new ArrayList<>(3);
    topics.add(HelperTest.createTopic(1L, "Topic #1", "Description #1"));
    topics.add(HelperTest.createTopic(2L, "Topic #2", "Description #2"));
    topics.add(HelperTest.createTopic(3L, "Topic #3", "Description #3"));
    given(topicService.findAll()).willReturn(topics);
    
    mockMvc.perform(get("/topics")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].title").isNotEmpty());
  }
  
  @Test
  void testFindById() throws Exception {
    Topic expected = HelperTest
        .createTopic(1L, "Pauta IT #1", "Description IT #1");
    given(topicService.findById(1L)).willReturn(Optional.of(expected));
    
    mockMvc.perform(get("/topics/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(expected.getId().intValue())))
        .andExpect(jsonPath("@.title", is(expected.getTitle())))
        .andExpect(jsonPath("@.description", is(expected.getDescription())))
        .andExpect(jsonPath("@.createdAt").isNotEmpty())
        .andDo(print());
    
    then(topicService).should().findById(1L);
  }
  
  @Test
  void testFindByIdNotFound() throws Exception {
    given(topicService.findById(1L)).willReturn(Optional.empty());
    
    mockMvc.perform(get("/topics/{id}", 999)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }
  
  @Test
  void testShouldCreate() throws Exception {
    TopicRequest topicRequest = HelperTest.createTopicRequest("Pauta #1", "Description #1");
    Topic topic = HelperTest
        .createTopic(1L, "Pauta #1", "Description #1");
    given(topicService.save(anyString(), anyString()))
        .willReturn(topic);
    
    mockMvc.perform(post("/topics")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(1)))
        .andExpect(jsonPath("@.title", is("Pauta #1")))
        .andExpect(jsonPath("@.description", is("Description #1")))
        .andExpect(jsonPath("@.createdAt").isNotEmpty())
        .andDo(print());
  }
  
  @Test
  void testUpdate() throws Exception {
    TopicRequest topicRequest = HelperTest.createTopicRequest("Pauta #2", "Description #2");
    Topic topic = HelperTest
        .createTopic(1L, "Pauta #2", "Description #2");
    given(topicService.update(eq(1L), anyString(), anyString()))
        .willReturn(topic);
    
    mockMvc.perform(put("/topics/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(1)))
        .andExpect(jsonPath("@.title", is("Pauta #2")))
        .andExpect(jsonPath("@.description", is("Description #2")))
        .andExpect(jsonPath("@.createdAt").isNotEmpty())
        .andDo(print());
  }
  
  @Test
  void testUpdateIdNotFound() throws Exception {
    TopicRequest topicRequest = HelperTest.createTopicRequest("Pauta #2", "Description #2");
    willThrow(DataNotFoundException.class).given(topicService)
        .update(eq(1L), anyString(), anyString());
    
    mockMvc.perform(put("/topics/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isNotFound())
        .andDo(print());
  }
  
  @Test
  void testDelete() throws Exception {
    Topic expected = HelperTest
        .createTopic(1L, "Pauta IT #1", "Description IT #1");
    given(topicService.findById(eq(1L))).willReturn(Optional.of(expected));
    
    mockMvc.perform(delete("/topics/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }
  
  @Test
  void testDeleteIdNotFound() throws Exception {
    willThrow(DataNotFoundException.class).given(topicService).delete(eq(999L));
    
    mockMvc.perform(delete("/topics/{id}", 999)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }
}
