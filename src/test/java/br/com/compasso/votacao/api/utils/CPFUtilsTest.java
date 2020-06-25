package br.com.compasso.votacao.api.utils;

import org.junit.jupiter.api.Test;

class CPFUtilsTest {
  
  private static int WEIGHT = 11;
  private static int CAPACITY_MAX = 11;
  private static int FACTOR_TO_CALCULATE = 2;
  private static int ZERO = 0;
  private static int SIZE_NUMBER_CPF = 11;
  private static int SIZE_PARTIAL_CPF = 9; //Body field CPF
  
  private static int FACTOR_ZERO_AT_TEN = 10;
  
  @Test
  public void testCPF() {
    String cpf = "3349924581";
    System.out.println(CPFUtils.isValid(cpf));
    
    
    /*for (int i = 0; i < 10; i++) {
      String cpf = CPFUtils.createNumberCpf();
      System.out.println("CPF gerado: " + cpf);
      boolean isValid = CPFUtils.isValid(cpf);
      System.out.println("CPF valido? :" + isValid);
    }*/
  }
} 
  
