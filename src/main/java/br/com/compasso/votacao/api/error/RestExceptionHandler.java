package br.com.compasso.votacao.api.error;

import br.com.compasso.votacao.api.exception.AssociateUnableForVotingException;
import br.com.compasso.votacao.api.exception.DataNotFoundException;
import br.com.compasso.votacao.api.exception.ExternalServiceUnavailableException;
import br.com.compasso.votacao.api.exception.InvalidCpfNumberException;
import br.com.compasso.votacao.api.exception.TopicWithExistingSessionException;
import br.com.compasso.votacao.api.exception.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    String error = "Malformed JSON request";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
    apiError.setMessage(error);
    apiError.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
    return errorResponseEntity(apiError);
  }
  
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    return errorResponseEntity(
        new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2),
            ex));
  }
  
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return errorResponseEntity(apiError);
  }
  
  
  @ExceptionHandler(javax.validation.ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
      javax.validation.ConstraintViolationException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getConstraintViolations());
    return errorResponseEntity(apiError);
  }
  
  @ExceptionHandler(DataNotFoundException.class)
  protected ResponseEntity<Object> handleDataNotFound(DataNotFoundException ex,
      WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
    apiError.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
    apiError.setMessage(ex.getMessage());
    return errorResponseEntity(apiError);
  }
  
  @ExceptionHandler(ExternalServiceUnavailableException.class)
  protected ResponseEntity<Object> handleExternalServiceUnavailable(
      ExternalServiceUnavailableException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex);
    apiError.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
    apiError.setMessage(ex.getMessage());
    return errorResponseEntity(apiError);
  }
  
  @ExceptionHandler({AssociateUnableForVotingException.class,
      TopicWithExistingSessionException.class,
      VoteAlreadyRegisteredException.class,
      VotingTimeSessionExpiredException.class})
  protected ResponseEntity<Object> handleBusinessException(RuntimeException ex,
      WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex);
    apiError.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
    apiError.setMessage(ex.getMessage());
    return errorResponseEntity(apiError);
  }
  
  @ExceptionHandler(InvalidCpfNumberException.class)
  protected ResponseEntity<Object> handleInvalidCpfNumber(InvalidCpfNumberException ex,
      WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
    apiError.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
    apiError.setMessage(ex.getMessage());
    return errorResponseEntity(apiError);
  }
  
  private ResponseEntity<Object> errorResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getHttpStatus());
  }
}
