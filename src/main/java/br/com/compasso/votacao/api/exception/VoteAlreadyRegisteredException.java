package br.com.compasso.votacao.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class VoteAlreadyRegisteredException extends Exception {
  
  public VoteAlreadyRegisteredException(String message) {
    this(message, (Throwable) null);
  }
  
  public VoteAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
