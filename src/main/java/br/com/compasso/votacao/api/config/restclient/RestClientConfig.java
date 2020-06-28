package br.com.compasso.votacao.api.config.restclient;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
  
  @Bean
  public RestTemplate restTemplate() {
    
    ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(
        new SimpleClientHttpRequestFactory());
    
    RestTemplate restTemplate = new RestTemplate(factory);
    
    restTemplate
        .setInterceptors(Collections.singletonList(new RestClientLoggingInterceptor()));
    
    return restTemplate;
  }
}
