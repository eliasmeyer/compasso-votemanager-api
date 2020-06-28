package br.com.compasso.votacao.api.exception;

public class ExternalServiceUnavailableException extends RuntimeException {
  
  public ExternalServiceUnavailableException(String message) {
    this(message, null);
  }
  
  public ExternalServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
