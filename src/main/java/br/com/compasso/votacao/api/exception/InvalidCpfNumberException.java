package br.com.compasso.votacao.api.exception;

public class InvalidCpfNumberException extends RuntimeException {
  
  public InvalidCpfNumberException(String message) {
    this(message, null);
  }
  
  public InvalidCpfNumberException(String message, Throwable cause) {
    super(message, cause);
  }
}
