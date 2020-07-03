package br.com.compasso.votacao.api.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Schema(name = "ApiError", description = "Response de Erro")
@Getter
@Setter
public class ApiError {
  
  private HttpStatus httpStatus;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private String message;
  private String path;
  private String debugMessage;
  private List<ApiSubError> subErrors;
  
  private ApiError() {
    timestamp = LocalDateTime.now();
  }
  
  public ApiError(HttpStatus httpStatus) {
    this();
    this.httpStatus = httpStatus;
  }
  
  public ApiError(HttpStatus httpStatus, String message, String path, Throwable ex) {
    this();
    this.httpStatus = httpStatus;
    this.message = message;
    this.path = path;
    this.debugMessage = ex.getLocalizedMessage();
  }
  
  public ApiError(HttpStatus httpStatus, Throwable ex) {
    this();
    this.httpStatus = httpStatus;
    this.message = Optional.ofNullable(ex.getMessage()).orElse("Unexpected error");
    this.debugMessage = ex.getLocalizedMessage();
  }
  
  public ApiError(HttpStatus httpStatus, String message, Throwable ex) {
    this();
    this.httpStatus = httpStatus;
    this.message = message;
    this.debugMessage = ex.getLocalizedMessage();
  }
  
  private void addSubError(ApiSubError subError) {
    if (subErrors == null) {
      subErrors = new ArrayList<>();
    }
    subErrors.add(subError);
  }
  
  private void addValidationError(String object, String field, Object rejectedValue,
      String message) {
    ApiValidationError apiValidationError = new ApiValidationError();
    apiValidationError.setObject(object);
    apiValidationError.setField(field);
    apiValidationError.setRejectedValue(rejectedValue);
    apiValidationError.setMessage(message);
    
    addSubError(apiValidationError);
  }
  
  private void addValidationError(String object, String message) {
    addSubError(new ApiValidationError(object, message));
  }
  
  private void addValidationError(FieldError fieldError) {
    this.addValidationError(
        fieldError.getObjectName(),
        fieldError.getField(),
        fieldError.getRejectedValue(),
        fieldError.getDefaultMessage());
  }
  
  public void addValidationErrors(List<FieldError> fieldErrors) {
    fieldErrors.forEach(this::addValidationError);
  }
  
  private void addValidationError(ObjectError objectError) {
    this.addValidationError(
        objectError.getObjectName(),
        objectError.getDefaultMessage());
  }
  
  public void addValidationError(List<ObjectError> globalErrors) {
    globalErrors.forEach(this::addValidationError);
  }
  
  private void addValidationError(ConstraintViolation<?> cv) {
    this.addValidationError(
        cv.getRootBeanClass().getSimpleName(),
        ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
        cv.getInvalidValue(),
        cv.getMessage());
  }
  
  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
    constraintViolations.forEach(this::addValidationError);
  }
}
