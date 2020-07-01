package br.com.compasso.votacao.api.exception;

public class VotingTimeSessionExpiredException extends RuntimeException {
  
  public VotingTimeSessionExpiredException(String message) {
    this(message, null);
  }
  
  public VotingTimeSessionExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}
