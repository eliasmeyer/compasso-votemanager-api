package br.com.compasso.votacao.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import br.com.compasso.votacao.api.enums.StatusToVote;
import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AssociateServiceTest {
  
  @Mock
  private RestTemplate restTemplate;
  @InjectMocks
  private AssociateService associateService;
  private static final String URI_REST = "https://user-info.herokuapp.com/users/{cpf}";
  
  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(associateService, "URI_REST", URI_REST);
  }
  
  @Test
  @DisplayName("Call External API Successfully")
  void testShouldCallAPISuccessfully() {
    AssociateResponse expected = new AssociateResponse(StatusToVote.ABLE_TO_VOTE.toString());
    given(restTemplate.getForObject(URI_REST, AssociateResponse.class, "12345678901"))
        .willReturn(expected);
    
    AssociateResponse actual = associateService.isAbleToVote("12345678901");
    
    assertThat(actual).isNotNull();
    then(restTemplate).should()
        .getForObject(eq(URI_REST), eq(AssociateResponse.class), eq("12345678901"));
  }
  
  @Test
  @DisplayName("API External with Response Empty")
  void testShouldThrowErrorResponseNull() {
    
    given(restTemplate.getForObject(URI_REST, AssociateResponse.class, "12345678901"))
        .willReturn(null);
    
    Throwable throwable = catchThrowable(() -> associateService.isAbleToVote("12345678901"));
    
    assertThat(throwable)
        .isInstanceOf(ExternalServiceUnavailableException.class);
  }
  
  @Test
  @DisplayName("Call External with Cpf wrong")
  void testShouldCallAPIWithCpfWrong() {
    willThrow(new InvalidCpfNumberException("Error"))
        .given(restTemplate).getForObject(URI_REST, AssociateResponse.class, "99999999999");
  
    Throwable throwable = catchThrowable(() -> associateService.isAbleToVote("99999999999"));
  
    assertThat(throwable)
        .isInstanceOf(InvalidCpfNumberException.class);
  }
  
  @Test
  @DisplayName("External API has Error Response Null")
  void testShouldThrowErrorAPIHasError() {
    given(restTemplate.getForObject(URI_REST, AssociateResponse.class, "99999999999"))
        .willReturn(null);
    
    Throwable throwable = catchThrowable(() -> associateService.isAbleToVote("99999999999"));
    
    assertThat(throwable)
        .isInstanceOf(ExternalServiceUnavailableException.class);
  }
  
  @Test
  @DisplayName("External API has Error HttpClient 404")
  void testShouldThrowErrorAPIHasErrorHttpClient404() {
    willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .given(restTemplate).getForObject(URI_REST, AssociateResponse.class, "99999999999");
    
    Throwable throwable = catchThrowable(() -> associateService.isAbleToVote("99999999999"));
    
    assertThat(throwable)
        .isInstanceOf(InvalidCpfNumberException.class);
  }
  
  @Test
  @DisplayName("External API has Error HttpClient 500")
  void testShouldThrowErrorAPIHasErrorHttpClient500() {
    willThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
        .given(restTemplate).getForObject(URI_REST, AssociateResponse.class, "99999999999");
    
    Throwable throwable = catchThrowable(() -> associateService.isAbleToVote("99999999999"));
    
    assertThat(throwable)
        .isInstanceOf(ExternalServiceUnavailableException.class);
  }
}
