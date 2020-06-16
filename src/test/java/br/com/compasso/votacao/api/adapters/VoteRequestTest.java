package br.com.compasso.votacao.api.adapters;

import br.com.compasso.votacao.api.enums.OptionVotation;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class VoteRequestTest {
  
  private static Validator validator;
  
  @BeforeAll
  public static void createValidator() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }
  
  @Test
  public void testIdAssociateIsNull() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setIdAssociate(null);
    voteRequest.setChoiceOfVote(OptionVotation.NAO);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @Test
  public void testIdAssociateIsNegative() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setIdAssociate(-123L);
    voteRequest.setChoiceOfVote(OptionVotation.NAO);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
  }
  
  @Test
  public void testIdAssociateVoteIsNull() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setIdAssociate(null);
    voteRequest.setChoiceOfVote(OptionVotation.NAO);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @ParameterizedTest
  @EnumSource(
      value = OptionVotation.class,
      names = {"SIM", "NAO"})
  public void testVoteRequestIsValid(OptionVotation optionVotation) {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setIdAssociate(123456789L);
    voteRequest.setChoiceOfVote(optionVotation);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertTrue(violations.isEmpty());
  }
  
  @Test
  public void testChoiceOfVoteInvalidOption() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setIdAssociate(123456789L);
    voteRequest.setChoiceOfVote(OptionVotation.INDETERMINADO);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
  }
  
  @Test
  public void testChoiceOfVoteIsNull() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setIdAssociate(123456789L);
    voteRequest.setChoiceOfVote(null);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
  }
}
