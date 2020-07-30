package br.com.compasso.votacao.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import br.com.compasso.votacao.api.enums.StatusToVote;
import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import io.netty.channel.ChannelOption;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.netty.http.client.HttpClient;

class AssociateServiceTest {
  
  final ObjectMapper objectMapper = new ObjectMapper();
  private MockWebServer mockWebServer;
  private AssociateService associateService;
  
  @BeforeEach
  public void setup() throws IOException {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
    HttpClient httpClient = HttpClient.create()
        .tcpConfiguration(tcpClient -> {
          tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1);
          return tcpClient;
        })
        .wiretap(true);
    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
    WebClient webClient = WebClient.builder()
        .clientConnector(connector)
        .baseUrl(mockWebServer.url("/").toString()).build();
    this.associateService = new AssociateService(webClient);
  }
  
  @Test
  void testShouldCallAPISuccessfully() throws JsonProcessingException {
    //given
    AssociateResponse mockAssociate = new AssociateResponse(StatusToVote.ABLE_TO_VOTE.toString());
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json")
        .setBody(objectMapper.writeValueAsString(mockAssociate))
        .setResponseCode(200);
    
    mockWebServer.enqueue(mockResponse);
    //when
    AssociateResponse actual = associateService.isAbleToVote("12345678901");
    
    //then
    assertThat(actual).isNotNull();
    assertThat(actual.getStatus()).isNotNull();
  }
  
  @Test
  void testShouldCallAPIWithErrorHttp404() throws JsonProcessingException {
    //given
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json")
        .setResponseCode(404);
    
    mockWebServer.enqueue(mockResponse);
    //when
    Throwable throwable = catchThrowable(
        () -> associateService.isAbleToVote("12345678901"));
    
    //then
    assertThat(throwable)
        .isInstanceOf(InvalidCpfNumberException.class);
  }
  
  @Test
  void testShouldCallAPIWithErrorHttp409() {
    //given
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json")
        .setResponseCode(409);
    
    mockWebServer.enqueue(mockResponse);
    //when
    Throwable throwable = catchThrowable(
        () -> associateService.isAbleToVote("12345678901"));
    
    //then
    assertThat(throwable)
        .isInstanceOf(ExternalServiceUnavailableException.class);
  }
  
  @Test
  void testShouldCallAPIWithErrorHttp500() {
    //given
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json")
        .setResponseCode(500);
    mockWebServer.enqueue(mockResponse);
    
    //when
    Throwable throwable = catchThrowable(
        () -> associateService.isAbleToVote("12345678901"));
    
    //then
    assertThat(throwable)
        .isInstanceOf(ExternalServiceUnavailableException.class);
  }
  
  @Test
  void testShouldCallAPIWithErrorConnectionTimeout() {
    //given
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json")
        .setBodyDelay(3L, TimeUnit.MILLISECONDS)
        .setResponseCode(500);
    mockWebServer.enqueue(mockResponse);
    
    //when
    Throwable throwable = catchThrowable(
        () -> associateService.isAbleToVote("12345678901"));
    
    //then
    assertThat(throwable)
        .isInstanceOf(ExternalServiceUnavailableException.class);
  }
}
