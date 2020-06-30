package br.com.compasso.votacao.api.exception;

public class TitleAlreadyRegisteredException extends RuntimeException {
  
  public TitleAlreadyRegisteredException(String message) {
    this(message, null);
  }
  
  public TitleAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
