package br.com.compasso.votacao.api.exceptions.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ApiValidationError extends ApiSubError {
  
  private String object;
  private String field;
  private Object rejectedValue;
  private String message;
  
  ApiValidationError(String object, String message) {
    this.object = object;
    this.message = message;
  }
}
