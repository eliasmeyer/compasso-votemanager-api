package br.com.compasso.votacao.api.controller;

import static br.com.compasso.votacao.api.helper.HelperTest.responseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.compasso.votacao.api.adapter.CpfResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CpfController.class)
class CpfControllerTest {
  
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  
  @Test
  void testGenerateCpfRandomSuccessfully() throws Exception {
    //given    
    
    //when
    MvcResult mvcResult = mockMvc.perform(get("/cpfs/random")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cpfNumber").isNotEmpty())
        .andExpect(jsonPath("$.isValid").isBoolean())
        .andReturn();
    
    CpfResponse actualResponse = objectMapper
        .readValue(mvcResult.getResponse().getContentAsString(), CpfResponse.class);
    
    //then
    assertThat(actualResponse.getCpfNumber()).describedAs("cpfNumber").isNotBlank();
    assertThat(actualResponse.getCpfNumber()).describedAs("cpfNumber").hasSize(11);
    assertThat(actualResponse.getIsValid()).describedAs("isValid").isTrue();
  }
  
  @Test
  void testCheckCpfSuccessfully() throws Exception {
    //given
    String pathParamCpf = "75982501892";
    
    //when
    MvcResult mvcResult = mockMvc.perform(get("/cpfs/test/{cpf}", pathParamCpf)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cpfNumber").isNotEmpty())
        .andExpect(jsonPath("$.isValid").isBoolean())
        .andReturn();
    
    CpfResponse actualResponse = objectMapper
        .readValue(mvcResult.getResponse().getContentAsString(), CpfResponse.class);
    
    //then
    assertThat(actualResponse.getCpfNumber()).describedAs("cpfNumber").isNotBlank();
    assertThat(actualResponse.getCpfNumber()).describedAs("cpfNumber").hasSize(11);
    assertThat(actualResponse.getIsValid()).describedAs("isValid").isFalse();
  }
  
  @ParameterizedTest
  @ValueSource(strings = {" ", "xpto", "123"})
  void testCheckCpfWithPathParamInvalid(String pathParamInvalid) throws Exception {
    //given
    
    //when + then
    mockMvc.perform(get("/cpfs/test/{cpf}", pathParamInvalid)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.subErrors").isNotEmpty())
        .andExpect(jsonPath("$.subErrors[:1].field").value("cpfNumber"))
        .andExpect(responseBody().containsError("cpf", "Cpf number is wrong"))
        .andReturn();
  }
}
