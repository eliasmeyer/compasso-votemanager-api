package br.com.compasso.votacao.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCpfNumberException extends RuntimeException {
  
  public InvalidCpfNumberException(String message) {
    this(message, (Throwable) null);
  }
  
  public InvalidCpfNumberException(String message, Throwable cause) {
    super(message, cause);
  }
}
