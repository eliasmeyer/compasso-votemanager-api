package br.com.compasso.votacao.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicWithExistingSessionException extends Exception {
  
  public TopicWithExistingSessionException(String message) {
    this(message, (Throwable) null);
  }
  
  public TopicWithExistingSessionException(String message, Throwable cause) {
    super(message, cause);
  }
}
