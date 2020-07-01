package br.com.compasso.votacao.api.exception;

public class VoteAlreadyRegisteredException extends RuntimeException {
  
  public VoteAlreadyRegisteredException(String message) {
    this(message, null);
  }
  
  public VoteAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
