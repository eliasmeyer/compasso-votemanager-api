package br.com.compasso.votacao.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends Exception {
  
  public DataNotFoundException() {
    this("Data not found!");
  }
  
  public DataNotFoundException(String message) {
    this(message, (Throwable) null);
  }
  
  public DataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
