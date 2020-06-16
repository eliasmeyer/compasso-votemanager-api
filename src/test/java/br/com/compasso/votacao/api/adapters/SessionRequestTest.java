package br.com.compasso.votacao.api.adapters;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SessionRequestTest {
  
  private static Validator validator;
  
  @BeforeAll
  public static void createValidator() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }
  
  @Test
  public void testIdTopicIsNull() {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setMinuteTimeVoting(1L);
    
    Set<ConstraintViolation<SessionRequest>> violations = validator.validate(sessionRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @Test
  public void testIdTopicIsNegative() {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setIdTopic(-123L);
    sessionRequest.setMinuteTimeVoting(1L);
    
    Set<ConstraintViolation<SessionRequest>> violations = validator.validate(sessionRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @Test
  public void testMinuteTimeVotingZero() {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setMinuteTimeVoting(0L);
    
    Set<ConstraintViolation<SessionRequest>> violations = validator.validate(sessionRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @Test
  public void testMinuteTimeVotingNegative() {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setMinuteTimeVoting(-123L);
    
    Set<ConstraintViolation<SessionRequest>> violations = validator.validate(sessionRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @Test
  public void testSessionRequestOk() {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setIdTopic(123L);
    sessionRequest.setMinuteTimeVoting(2L);
    
    Set<ConstraintViolation<SessionRequest>> violations = validator.validate(sessionRequest);
    Assertions.assertTrue(violations.isEmpty());
    
  }
}
