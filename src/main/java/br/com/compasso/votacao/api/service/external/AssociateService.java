package br.com.compasso.votacao.api.service.external;

import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.client.WebClient;

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
  
    return
        webClient
            .get()
            .uri("/users/{cpf}", numberCpf)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> {
              log.error("CPF number [{}] is INVALID!", numberCpf);
              throw new InvalidCpfNumberException("CPF number is invalid!");
            })
            .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
              log.error("ERROR WHEN CONSUMING THE EXTERNAL API - HTTP CODE 4XX CLIENT ERROR");
              throw new ExternalServiceUnavailableException(
                  "ERROR when consuming the external API - HTTP CODE 4XX CLIENT ERROR");
            })
            .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
              log.error("ERROR WHEN CONSUMING THE EXTERNAL API - 5XX SERVER ERROR");
              throw new ExternalServiceUnavailableException(
                  "ERROR when consuming the external API - HTTP CODE 5XX SERVER ERROR");
            })
            .bodyToMono(AssociateResponse.class)
            //If there is any other type of return or if you cannot connect to the url, the return is empty
            //causes error on BodyExtractors - readWithMessageReaders
            .onErrorMap(UnsupportedMediaTypeException.class,
                throwable -> {
                  log.error("ERROR WHEN CONSUMING THE EXTERNAL API", throwable);
                  return new ExternalServiceUnavailableException(
                      "ERROR WHEN CONSUMING THE EXTERNAL API",
                      throwable);
                })
            .block();
  }
}
