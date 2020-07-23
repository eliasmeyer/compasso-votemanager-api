package br.com.compasso.votacao.api.config.webClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {
  
  @Value("${compasso.votemanager.urlbase.api.cpf}")
  private String URL_BASE;
  
  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .baseUrl(URL_BASE)
        .clientConnector(new ReactorClientHttpConnector(
            HttpClient.create().wiretap(true)
        ))
        .build();
  }
}

