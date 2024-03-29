package br.com.compasso.votacao.api.controller.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumNamePatternValidator implements ConstraintValidator<EnunNamePattern, Enum<?>> {
  
  private Pattern pattern;
  @Override
  public void initialize(EnunNamePattern annotation) {
    try {
      pattern = Pattern.compile(annotation.regexp());
    } catch (PatternSyntaxException e) {
      throw new IllegalArgumentException("Given regex is invalid", e);
    }
  }
  
  @Override
  public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    
    Matcher m = pattern.matcher(value.name());
    return m.matches();
  }
}
