package br.com.compasso.votacao.api.service.external;

import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AssociateService {
  
  @Value("${compasso.votemanager.url.api.cpf}")
  private String URI_REST;
  private RestTemplate restTemplate;
  
  @Autowired
  public AssociateService() {
    this.restTemplate = new RestTemplateBuilder().build();
  }
  
  public AssociateResponse isAbleToVote(final String numberCpf) {
    try {
      Optional<AssociateResponse> response = Optional.ofNullable(
          restTemplate.getForObject(
              URI_REST,
              AssociateResponse.class,
              numberCpf)
      );
      if (!response.isPresent()) {
        throw new ExternalServiceUnavailableException("Response body is null!");
      }
      return response.get();
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new InvalidCpfNumberException("CPF number is invalid!");
      }
      if (ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
        throw new ExternalServiceUnavailableException("ERROR in Service External Associate", ex);
      }
    }
    return null;
  }
}
