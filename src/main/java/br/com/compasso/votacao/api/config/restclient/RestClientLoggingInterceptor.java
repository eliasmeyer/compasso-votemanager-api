package br.com.compasso.votacao.api.config.restclient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

@Slf4j
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {
  
  @Override
  public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body,
      ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    logRequest(httpRequest, body);
    ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, body);
    logResponse(response);
    return response;
  }
  
  private void logRequest(HttpRequest request, byte[] body) throws IOException {
    log.info(
        "===========================request begin================================================");
    log.debug("URI         : {}", request.getURI());
    log.debug("Method      : {}", request.getMethod());
    log.debug("Headers     : {}", request.getHeaders());
    log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
    log.info(
        "==========================request end================================================");
  }
  
  private void logResponse(ClientHttpResponse response) throws IOException {
    log.info(
        "============================response begin==========================================");
    log.debug("Status code  : {}", response.getStatusCode());
    log.debug("Status text  : {}", response.getStatusText());
    log.debug("Headers      : {}", response.getHeaders());
    log.debug("Response body: {}",
        StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
    log.info(
        "=======================response end=================================================");
  }
}
