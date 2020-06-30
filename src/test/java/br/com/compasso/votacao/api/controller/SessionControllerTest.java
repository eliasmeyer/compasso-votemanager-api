package br.com.compasso.votacao.api.controller;

import static br.com.compasso.votacao.api.helper.HelperTest.createSession;
import static br.com.compasso.votacao.api.helper.HelperTest.createSessionRequest;
import static br.com.compasso.votacao.api.helper.HelperTest.createVoteRequest;
import static br.com.compasso.votacao.api.helper.HelperTest.ignoreFields;
import static br.com.compasso.votacao.api.helper.HelperTest.responseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.compasso.votacao.api.adapter.SessionRequest;
import br.com.compasso.votacao.api.adapter.VoteRequest;
import br.com.compasso.votacao.api.config.MapperConfig;
import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.error.ApiError;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.helper.ApiErrorDeserializer;
import br.com.compasso.votacao.api.mapper.SessionMapper;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
@WebMvcTest(SessionController.class)
@Import(MapperConfig.class)
class SessionControllerTest {
  
  @Autowired
  private MockMvc mockMvc;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private SessionMapper sessionMapper;
  
  @MockBean
  private SessionService sessionService;
  
  private static final SimpleModule module = new SimpleModule();
  
  @BeforeEach
  public void setUp() {
    module.addDeserializer(ApiError.class, new ApiErrorDeserializer());
    objectMapper.registerModule(module);
  }
  
  @Test
  void testFindAllSuccessfully() throws Exception {
    List<Session> expectedResponse = new ArrayList<>(3);
    expectedResponse.add(createSession(1L, 1L));
    expectedResponse.add(createSession(2L, 1L));
    expectedResponse.add(createSession(3L, 1L));
    given(sessionService.findAll()).willReturn(expectedResponse);
    
    MvcResult mvcResult = mockMvc.perform(get("/sessions")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].dateTimeOpening").isNotEmpty())
        .andExpect(jsonPath("$[0].dateTimeClosing").isNotEmpty())
        .andExpect(jsonPath("$[0].statusSession").isNotEmpty())
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    assertThat(actualResult)
        .isEqualToIgnoringWhitespace(
            objectMapper.writeValueAsString(sessionMapper.from(expectedResponse)));
  }
  
  @Test
  void testFindByIdSuccessfully() throws Exception {
    Session expectedResponse = createSession(1L, 1L);
    given(sessionService.findById(1L)).willReturn(Optional.of(expectedResponse));
    
    MvcResult mvcResult = mockMvc.perform(get("/sessions/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(1)))
        .andExpect(jsonPath("@.dateTimeOpening").isNotEmpty())
        .andExpect(jsonPath("@.dateTimeClosing").isNotEmpty())
        .andExpect(jsonPath("@.statusSession", is(StatusSession.ABERTO.toString())))
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    
    then(sessionService).should().findById(1L);
    assertThat(actualResult)
        .isEqualToIgnoringWhitespace(
            objectMapper.writeValueAsString(sessionMapper.from(expectedResponse)));
  }
  
  @Test
  void testFindByIdWithIdNotFound() throws Exception {
    ApiError expectedResponse = new ApiError(HttpStatus.NOT_FOUND,
        new DataNotFoundException("Session not found on 999"));
    given(sessionService.findById(999L)).willReturn(Optional.empty());
    
    MvcResult mvcResult = mockMvc.perform(get("/sessions/{id}", 999)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
    then(sessionService).should().findById(999L);
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
    
  }
  
  @Test
  void testCreateSuccessfully() throws Exception {
    SessionRequest sessionRequest = createSessionRequest(1L, 2L);
    Session expectedResponse = createSession(1L, 2L);
    given(sessionService.open(1L, 2L)).willReturn(expectedResponse);
    
    MvcResult mvcResult = mockMvc.perform(post("/sessions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sessionRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.id", is(1)))
        .andExpect(jsonPath("@.dateTimeOpening").isNotEmpty())
        .andExpect(jsonPath("@.dateTimeClosing").isNotEmpty())
        .andExpect(jsonPath("@.statusSession", is(StatusSession.ABERTO.toString())))
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(sessionMapper.from(expectedResponse));
    then(sessionService).should().open(1L, 2L);
    assertEquals(expectedResult, actualResult,
        ignoreFields("dateTimeOpening", "dateTimeClosing"));
  }
  
  @Test
  void testCreateWithTopicIdNotFound() throws Exception {
    SessionRequest sessionRequest = createSessionRequest(999L, 2L);
    ApiError expectedResponse = new ApiError(HttpStatus.NOT_FOUND,
        new DataNotFoundException("Topic not found on 999"));
    
    willThrow(new DataNotFoundException("Topic not found on 999"))
        .given(sessionService).open(999L, 2L);
    
    MvcResult mvcResult = mockMvc.perform(post("/sessions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sessionRequest)))
        .andExpect(status().isNotFound())
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
    
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
  }
  
  @Test
  void testCreateWithTopicIdExistingSession() throws Exception {
    SessionRequest sessionRequest = createSessionRequest(1L, 2L);
    Session expectedSession = createSession(1L, 2L);
    ApiError expectedResponse = new ApiError(HttpStatus.CONFLICT,
        new TopicWithExistingSessionException("Topic with session already registered"));
    
    given(sessionService.findById(1L)).willReturn(Optional.of(expectedSession));
    willThrow(new TopicWithExistingSessionException("Topic with session already registered"))
        .given(sessionService).open(1L, 2L);
    
    MvcResult mvcResult = mockMvc.perform(post("/sessions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sessionRequest)))
        .andExpect(status().isConflict())
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
    
    then(sessionService).should(never()).findById(1L);
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
  }
  
  @Test
  void testVoteSuccessfully() throws Exception {
    VoteRequest voteRequest = createVoteRequest("12345678901", OptionVotation.SIM);
    willDoNothing().given(sessionService).vote(1L, "12345678901", OptionVotation.SIM);
    
    mockMvc.perform(post("/sessions/{id}/votes", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(voteRequest)))
        .andExpect(status().isOk())
        .andReturn();
    
    then(sessionService).should(times(1)).vote(eq(1L), eq("12345678901"), eq(OptionVotation.SIM));
  }
  
  @Test
  void testVoteWithSessionIdNotFound() throws Exception {
    ApiError expectedResponse = new ApiError(HttpStatus.NOT_FOUND,
        new DataNotFoundException("Session not found on 999"));
    VoteRequest voteRequest = createVoteRequest("12345678901", OptionVotation.SIM);
    willThrow(new DataNotFoundException("Session not found on 999")).given(sessionService)
        .vote(anyLong(), anyString(), any(OptionVotation.class));
    
    MvcResult mvcResult = mockMvc.perform(post("/sessions/{id}/votes", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(voteRequest)))
        .andExpect(status().isNotFound())
        .andReturn();
    
    String actualResult = mvcResult.getResponse().getContentAsString();
    String expectedResult = objectMapper.writeValueAsString(expectedResponse);
    
    assertEquals(expectedResult, actualResult, ignoreFields("timestamp", "path"));
  }
  
  @Test
  void testVoteWithRequestCpfNull() throws Exception {
    VoteRequest voteRequest = createVoteRequest(null, OptionVotation.SIM);
    
    MvcResult mvcResult = mockMvc.perform(post("/sessions/{id}/votes", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(voteRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(responseBody().containsError("cpf", "Cpf can't be blank or null"))
        .andReturn();
    
    then(sessionService).should(never()).findById(999L);
  }
  
  @Test
  void testVoteWithRequestChoiceVoteNull() throws Exception {
    VoteRequest voteRequest = createVoteRequest("12345678901", null);
    given(sessionService.findById(999L)).willReturn(Optional.empty());
    
    mockMvc.perform(post("/sessions/{id}/votes", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(voteRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.subErrors").isNotEmpty())
        .andDo(print());
    
    then(sessionService).should(never()).findById(999L);
  }
  
  @Test
  void testVoteWithRequestChoiceIndeterminado() throws Exception {
    VoteRequest voteRequest = createVoteRequest("12345678901", null);
    given(sessionService.findById(999L)).willReturn(Optional.empty());
    
    MvcResult mvcResult = mockMvc.perform(post("/sessions/{id}/votes", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(voteRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.subErrors").isNotEmpty())
        .andExpect(responseBody().containsError("vote", "Vote should be SIM or NAO"))
        .andReturn();
    
    then(sessionService).should(never()).findById(999L);
  }
  
  @Test
  void testCreateWithSessionRequestTopicIdNull() throws Exception {
    SessionRequest sessionRequest = createSessionRequest(null, null);
    
    mockMvc.perform(post("/sessions", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sessionRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.subErrors").isNotEmpty())
        .andExpect(responseBody().containsError("topicId", "topicId can't be null"))
        .andReturn();
    
  }
  
  @Test
  void testCreateWithSessionRequestMinuteTimeVotingNegative() throws Exception {
    SessionRequest sessionRequest = createSessionRequest(1L, -1L);
    
    mockMvc.perform(post("/sessions", 999)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sessionRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.subErrors").isNotEmpty())
        .andExpect(responseBody().containsError("minuteTimeVoting",
            "minuteTimeVoting should be greater than or equal 1"))
        .andReturn();
    
  }
}
