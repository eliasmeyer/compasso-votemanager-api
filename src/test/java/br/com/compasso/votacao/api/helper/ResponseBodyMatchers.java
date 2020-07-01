package br.com.compasso.votacao.api.helper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.error.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.test.web.servlet.ResultMatcher;

public class ResponseBodyMatchers {
  
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final SimpleModule module = new SimpleModule();
  
  public <T> ResultMatcher containsObjectAsJson(
      Object expectedObject,
      Class<T> targetClass) {
    return mvcResult -> {
      String json = mvcResult.getResponse().getContentAsString();
      T actualObject = objectMapper.readValue(json, targetClass);
      assertThat(actualObject).isEqualToComparingFieldByField(expectedObject);
    };
  }
  
  public ResultMatcher containsError(
      String expectedFieldName,
      String expectedMessage) {
    return mvcResult -> {
      module.addDeserializer(ApiError.class, new ApiErrorDeserializer());
      objectMapper.registerModule(module);
      String json = mvcResult.getResponse().getContentAsString();
      ApiError apiErrorTest = objectMapper.readValue(json, ApiError.class);
      
      assertThat(apiErrorTest.getSubErrors())
          .hasSize(1)
          .withFailMessage("Expecting exactly 1 error message "
                  + "with field name '%s' and message '%s'",
              expectedFieldName,
              expectedMessage);
    };
  }
}
