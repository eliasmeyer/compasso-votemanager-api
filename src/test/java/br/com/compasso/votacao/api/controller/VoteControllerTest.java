package br.com.compasso.votacao.api.controller;

import static br.com.compasso.votacao.api.helper.HelperTest.createSession;
import static br.com.compasso.votacao.api.helper.HelperTest.createVote;
import static br.com.compasso.votacao.api.helper.HelperTest.responseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.compasso.votacao.api.adapter.VoteResponse;
import br.com.compasso.votacao.api.config.MapperConfig;
import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.mapper.VoteMapper;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.service.VoteService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VoteController.class)
@Import(MapperConfig.class)
class VoteControllerTest {
  
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private VoteService voteService;
  @Autowired
  private VoteMapper voteMapper;
  
  @Test
  void testFindAllBySessionIdSuccessfully() throws Exception {
    Session session = createSession(1L, 2L);
    List<Vote> votes = new ArrayList<>(3);
    votes.add(createVote(session, "12345678901", OptionVotation.SIM));
    votes.add(createVote(session, "01987654321", OptionVotation.SIM));
    votes.add(createVote(session, "45612378901", OptionVotation.NAO));
    List<VoteResponse> expectedResponse = voteMapper.from(votes);
    given(voteService.findAllBySessionId(1L)).willReturn(votes);
    
    MvcResult mvcResult = mockMvc.perform(get("/sessions/{id}/votes", 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    
    String actualJsonResult = mvcResult.getResponse().getContentAsString();
    List<VoteResponse> actualListResult = objectMapper.readValue(
        actualJsonResult,
        new TypeReference<List<VoteResponse>>() {
        }
    );
    
    assertThat(actualListResult).hasSize(3);
    assertThat(actualJsonResult)
        .isEqualToIgnoringWhitespace(
            objectMapper.writeValueAsString(expectedResponse));
    
    then(voteService).should().findAllBySessionId(1L);
  }
  
  @Test
  void testFindBySessionIdAndAssociateIdSuccessfully() throws Exception {
    Session session = createSession(1L, 2L);
    Vote vote = createVote(session, "12345678901", OptionVotation.NAO);
    VoteResponse expectedResponse = voteMapper.from(vote);
    given(voteService.findBySessionIdAndCpfNumber(1L, "12345678901")).willReturn(vote);
    
    MvcResult mvcResult = mockMvc.perform(get("/sessions/{id}/votes/associates", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .param("cpf", "12345678901"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("@.cpf", is("12345678901")))
        .andReturn();
    
    String actualJsonResult = mvcResult.getResponse().getContentAsString();
    assertThat(actualJsonResult)
        .isEqualToIgnoringWhitespace(
            objectMapper.writeValueAsString(expectedResponse));
    
    then(voteService).should().findBySessionIdAndCpfNumber(eq(1L), eq("12345678901"));
  }
  
  @Test
  void testFindBySessionIdAndAssociateParamCpfIsWrong() throws Exception {
    Session session = createSession(1L, 2L);
    Vote vote = createVote(session, "12345678901", OptionVotation.NAO);
    VoteResponse expectedResponse = voteMapper.from(vote);
    given(voteService.findBySessionIdAndCpfNumber(1L, "12345678901")).willReturn(vote);
    
    mockMvc.perform(get("/sessions/{id}/votes/associates", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .param("cpf", "xpto"))
        .andExpect(status().isBadRequest())
        .andExpect(responseBody().containsError("cpf", "RequestParam 'cpf' is wrong"))
        .andReturn();
  }
  
  @Test
  void testFindBySessionIdAndAssociatePathVariableWrong() throws Exception {
    Session session = createSession(1L, 2L);
    Vote vote = createVote(session, "12345678901", OptionVotation.NAO);
    VoteResponse expectedResponse = voteMapper.from(vote);
    given(voteService.findBySessionIdAndCpfNumber(1L, "12345678901")).willReturn(vote);
    
    mockMvc.perform(get("/sessions/{id}/votes/associates", -1)
        .contentType(MediaType.APPLICATION_JSON)
        .param("cpf", "12345678901"))
        .andExpect(status().isBadRequest())
        .andExpect(responseBody().containsError("id", "id is invalid"))
        .andReturn();
  }
}
