package br.com.compasso.votacao.api.exception;

public class AssociateUnableForVotingException extends RuntimeException {
  
  public AssociateUnableForVotingException(String message) {
    super(message);
  }
  
  public AssociateUnableForVotingException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public AssociateUnableForVotingException(Throwable cause) {
    super(cause);
  }
}
