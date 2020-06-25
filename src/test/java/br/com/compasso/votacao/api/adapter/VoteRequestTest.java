package br.com.compasso.votacao.api.adapter;

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
  public void testCPFIsNull() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setCpf(null);
    voteRequest.setVote(OptionVotation.NAO);
    
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
    
  }
  
  @ParameterizedTest
  @EnumSource(
      value = OptionVotation.class,
      names = {"SIM", "NAO"})
  public void testVoteRequestIsValid(OptionVotation optionVotation) {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setCpf("12345678901");
    voteRequest.setVote(optionVotation);
  
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertTrue(violations.isEmpty());
  }
  
  @Test
  public void testChoiceOfVoteInvalidOption() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setCpf("12345678901");
    voteRequest.setVote(OptionVotation.INDETERMINADO);
  
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
  }
  
  @Test
  public void testChoiceOfVoteIsNull() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setCpf("12345678901");
    voteRequest.setVote(null);
  
    Set<ConstraintViolation<VoteRequest>> violations = validator.validate(voteRequest);
    Assertions.assertFalse(violations.isEmpty());
  }
}
