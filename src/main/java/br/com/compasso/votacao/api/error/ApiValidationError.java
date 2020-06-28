package br.com.compasso.votacao.api.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
