package br.com.compasso.votacao.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalServiceUnavailableException extends RuntimeException {
  
  public ExternalServiceUnavailableException(String message) {
    this(message, (Throwable) null);
  }
  
  public ExternalServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
