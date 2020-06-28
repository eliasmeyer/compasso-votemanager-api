package br.com.compasso.votacao.api.exception;

public class DataNotFoundException extends RuntimeException {
  
  public DataNotFoundException() {
    this("Data not found!");
  }
  
  public DataNotFoundException(String message) {
    this(message, null);
  }
  
  public DataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
