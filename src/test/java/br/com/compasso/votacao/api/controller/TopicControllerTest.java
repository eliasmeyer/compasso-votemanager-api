package br.com.compasso.votacao.api.controller;

import static br.com.compasso.votacao.api.helper.TestHelper.createTopic;
import static br.com.compasso.votacao.api.helper.TestHelper.createTopicRequest;
import static br.com.compasso.votacao.api.helper.TestHelper.ignoreFields;
import static br.com.compasso.votacao.api.helper.TestHelper.responseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.compasso.votacao.api.adapter.TopicRequest;
import br.com.compasso.votacao.api.adapter.TopicResponse;
import br.com.compasso.votacao.api.config.MapperConfig;
import br.com.compasso.votacao.api.error.ApiError;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TitleAlreadyRegisteredException;
import br.com.compasso.votacao.api.mapper.TopicMapper;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.service.TopicService;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
  @Autowired
  private TopicMapper topicMapper;
  
  @Test
  void testFindAllSuccessfully() throws Exception {
    List<Topic> topics = new ArrayList<>(3);
    topics.add(createTopic(1L, "Topic #1", "Description #1"));
    topics.add(createTopic(2L, "Topic #2", "Description #2"));
    topics.add(createTopic(3L, "Topic #3", "Description #3"));
    List<TopicResponse> expectedResponse = topicMapper.from(topics);
    given(topicService.findAll()).willReturn(topics);
    
    MvcResult mvcResult = mockMvc.perform(get("/topics")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    
    String actualJsonResult = mvcResult.getResponse().getContentAsString();
    List<TopicResponse> actualListResult = objectMapper.readValue(
        actualJsonResult,
        new TypeReference<List<TopicResponse>>() {
        }
    );
    
    assertThat(actualListResult).hasSize(3);
    assertThat(actualJsonResult)
        .isEqualToIgnoringWhitespace(
            objectMapper.writeValueAsString(expectedResponse));
  }
  
  @Test
  void testFindByIdSuccessfully() throws Exception {
    Topic topic = createTopic(1L, "Pauta IT #1", "Description IT #1");
    TopicResponse expectedResponse = topicMapper.from(topic);
    given(topicService.findOneWithResultById(1L)).willReturn(Optional.of(topic));
    
    MvcResult mvcResult = mockMvc.perform(get("/topics/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(topic.getId().intValue())))
        .andExpect(jsonPath("@.title", is(topic.getTitle())))
        .andExpect(jsonPath("@.description", is(topic.getDescription())))
        .andExpect(jsonPath("@.createdAt").isNotEmpty())
        .andReturn();
    
    String actualJsonResult = mvcResult.getResponse().getContentAsString();
    assertThat(actualJsonResult)
        .isEqualToIgnoringWhitespace(
            objectMapper.writeValueAsString(expectedResponse));
  
    then(topicService).should().findOneWithResultById(1L);
  }
  
  @Test
  void testFindByIdNotFound() throws Exception {
    ApiError expectedResponse = new ApiError(HttpStatus.NOT_FOUND,
        new DataNotFoundException("Topic not found on 999"));
    given(topicService.findById(999L)).willReturn(Optional.empty());
  
    MvcResult mvcResult = mockMvc.perform(get("/topics/{id}", 999)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
  }
  
  @Test
  void testCreateSuccessfully() throws Exception {
    TopicRequest topicRequest = createTopicRequest("Pauta #1", "Description #1");
    Topic topic = createTopic(1L, "Pauta #1", "Description #1");
    given(topicService.save(anyString(), anyString()))
        .willReturn(topic);
    
    MvcResult mvcResult = mockMvc.perform(post("/topics")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(1)))
        .andExpect(jsonPath("@.title", is("Pauta #1")))
        .andExpect(jsonPath("@.description", is("Description #1")))
        .andExpect(jsonPath("@.createdAt").isNotEmpty())
        .andReturn();
  
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(topicMapper.from(topic));
    then(topicService).should().save(anyString(), anyString());
    assertEquals(expectedResult, actualResult,
        ignoreFields("createdAt"));
  }
  
  @Test
  void testCreateWitTitleEquals() throws Exception {
    TopicRequest topicRequest = createTopicRequest("Pauta #1", "Description #1");
    ApiError expectedResponse = new ApiError(HttpStatus.CONFLICT,
        new TitleAlreadyRegisteredException("Topic with title already registered"));
    willThrow(new TitleAlreadyRegisteredException("Topic with title already registered"))
        .given(topicService)
        .save(anyString(), anyString());
    
    MvcResult mvcResult = mockMvc.perform(post("/topics")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isConflict())
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
    
    then(topicService).should().save(anyString(), anyString());
    assertEquals(expectedResult, actualResult,
        ignoreFields("timestamp", "path"));
  }
  
  @Test
  void testCreateWithRequestInvalid() throws Exception {
    TopicRequest topicRequest = createTopicRequest(null, "Description #1");
    
    mockMvc.perform(post("/topics")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.subErrors").isNotEmpty())
        .andExpect(responseBody().containsError("title", "title can't be null"))
        .andReturn();
    
    then(topicService).should(never()).save(anyString(), anyString());
    
  }
  
  @Test
  void testUpdateSuccessfully() throws Exception {
    TopicRequest topicRequest = createTopicRequest("Pauta #2", "Description #2");
    Topic topic = createTopic(1L, "Pauta #2", "Description #2");
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
    TopicRequest topicRequest = createTopicRequest("Pauta #2", "Description #2");
    ApiError expectedResponse = new ApiError(HttpStatus.NOT_FOUND,
        new DataNotFoundException("Topic not found on 999"));
    willThrow(new DataNotFoundException("Topic not found on 999")).given(topicService)
        .update(eq(999L), anyString(), anyString());
  
    MvcResult mvcResult = mockMvc.perform(put("/topics/{id}", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(topicRequest)))
        .andExpect(status().isNotFound())
        .andReturn();
  
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
  
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
  }
  
  @Test
  void testDeleteSucessfully() throws Exception {
    Topic expected = createTopic(1L, "Pauta IT #1", "Description IT #1");
    given(topicService.findById(eq(1L))).willReturn(Optional.of(expected));
    
    mockMvc.perform(delete("/topics/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }
  
  @Test
  void testDeleteIdNotFound() throws Exception {
    ApiError expectedResponse = new ApiError(HttpStatus.NOT_FOUND,
        new DataNotFoundException("Topic not found on 999"));
    willThrow(new DataNotFoundException("Topic not found on 999")).given(topicService)
        .delete(eq(999L));
  
    MvcResult mvcResult = mockMvc.perform(delete("/topics/{id}", 999)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
  
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
  }
}
