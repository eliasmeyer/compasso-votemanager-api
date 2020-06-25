package br.com.compasso.votacao.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VotingTimeSessionExpiredException extends Exception {
  
  public VotingTimeSessionExpiredException(String message) {
    this(message, (Throwable) null);
  }
  
  public VotingTimeSessionExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}
