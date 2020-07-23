package br.com.compasso.votacao.api.service.external;

import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class AssociateService {
  
  private final WebClient webClient;
  
  @Autowired
  public AssociateService(WebClient webClient) {
    this.webClient = webClient;
  }
  
  public AssociateResponse isAbleToVote(final String numberCpf) {
    log.debug("Checking cpf [{}] on EXTERNAL API", numberCpf);
    try {
      return
          webClient
              .get()
              .uri("/users/{cpf}", numberCpf)
              .accept(MediaType.APPLICATION_JSON)
              .retrieve()
              .bodyToMono(AssociateResponse.class)
              .block();
    } catch (WebClientResponseException ex) {
      
      if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
        log.error("CPF number [{}] is INVALID!", numberCpf);
        throw new InvalidCpfNumberException("CPF number is invalid!");
      }
      log.error("ERROR EXTERNAL API", ex);
      throw new ExternalServiceUnavailableException("ERROR in Service External Associate", ex);
    }
  }
}
