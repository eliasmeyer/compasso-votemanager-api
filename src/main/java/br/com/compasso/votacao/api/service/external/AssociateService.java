package br.com.compasso.votacao.api.service.external;

import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AssociateService {
  
  @Value("${compasso.votemanager.url.api.cpf}")
  private String URI_REST;
  @Autowired
  private RestTemplate restTemplate;
  
  public AssociateResponse isAbleToVote(final String numberCpf) {
    log.debug("Checking cpf [{}] on EXTERNAL API", numberCpf);
  
    restTemplate.getForObject(
        URI_REST,
        AssociateResponse.class,
        numberCpf);
  
    try {
      return Optional.ofNullable(
          restTemplate.getForObject(
              URI_REST,
              AssociateResponse.class,
              numberCpf)
      ).orElseThrow(() -> {
        log.error("ERROR RETURN EXTERNAL API - RESPONSE NULL");
        return new ExternalServiceUnavailableException("Response body is null!");
      });
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.warn("CPF number [{}] is INVALID!");
        throw new InvalidCpfNumberException("CPF number is invalid!");
      }
      if (ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
        log.error("ERROR EXTERNAL API");
        throw new ExternalServiceUnavailableException("ERROR in Service External Associate", ex);
      }
    }
    return null;
  }
}
