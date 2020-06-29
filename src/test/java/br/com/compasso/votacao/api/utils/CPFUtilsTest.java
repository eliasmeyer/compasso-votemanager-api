package br.com.compasso.votacao.api.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CPFUtilsTest {
  
  @Test
  @DisplayName("CPF number is valid")
  void testCPFShouldBeValid() {
    String cpf = "33499245817";
    Assertions.assertThat(CPFUtils.isValid(cpf)).isTrue();
  }
  
  @Test
  @DisplayName("CPF number isn't valid")
  void testCPFShouldBeInvalid() {
    String cpf = "33499245818";
    Assertions.assertThat(CPFUtils.isValid(cpf)).isFalse();
  }
  
  @Test
  @DisplayName("Generate CPF number valid")
  void testShouldGenerateCpfNumberValid() {
    String cpf = CPFUtils.createNumberCpf();
    Assertions.assertThat(CPFUtils.isValid(cpf)).isTrue();
  }
} 
  
