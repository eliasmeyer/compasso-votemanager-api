package br.com.compasso.votacao.api.exception;

public class TopicWithExistingSessionException extends RuntimeException {
  
  public TopicWithExistingSessionException(String message) {
    this(message, null);
  }
  
  public TopicWithExistingSessionException(String message, Throwable cause) {
    super(message, cause);
  }
}
