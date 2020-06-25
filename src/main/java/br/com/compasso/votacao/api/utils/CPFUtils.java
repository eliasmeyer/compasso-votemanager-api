package br.com.compasso.votacao.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CPFUtils {
  
  private static int ZERO = 0;
  private static int ONE = 1;
  private static int NUMBER_TO_TEN = 10;
  //Body CPF - without digits
  private static int SIZE_PARTIAL_CPF = 9; //Body field CPF
  private static int FACTOR_TO_CALCULATE = 2;
  private static int CAPACITY_MAX_NUMBER_CPF = 11;
  private static int INDEX_FIRST_DIGIT_VERIFIER = 9;
  private static int INDEX_SECOND_DIGIT_VERIFIER = 10;
  private static String SPLIT_CHARACTER = "";
  
  //Private constructor
  private CPFUtils() {
  }
  
  List<Integer> doGenerate() {
    List<Integer> listNumbersCpf = new ArrayList<>(CAPACITY_MAX_NUMBER_CPF);
    List<Integer> parcialCpf = this.generateRandomBody();
    Integer firstDigit = this.calculateDigitVerifier(parcialCpf);
    
    listNumbersCpf.addAll(parcialCpf);
    listNumbersCpf.add(firstDigit);
    int secondDigit = this.calculateDigitVerifier(listNumbersCpf);
    listNumbersCpf.add(secondDigit);
    
    return listNumbersCpf;
  }
  
  List<Integer> generateRandomBody() {
    IntStream intStream = IntStream
        .generate(() -> {
          return (int) (Math.random() * NUMBER_TO_TEN);
        });
    List<Integer> randomNumbers = intStream.limit(SIZE_PARTIAL_CPF).boxed()
        .collect(Collectors.toList());
    return randomNumbers;
  }
  
  int calculateDigitVerifier(List<Integer> partialCpf) {
    Objects.requireNonNull(partialCpf);
    int indexMax = partialCpf.size() + ONE;
    AtomicInteger index = new AtomicInteger(indexMax);
    int total = partialCpf
        .stream()
        .mapToInt(i -> i * index.getAndDecrement())
        .sum();
    
    int value = total % CAPACITY_MAX_NUMBER_CPF;
    
    if (value < FACTOR_TO_CALCULATE) {
      return ZERO;
    }
    
    return CAPACITY_MAX_NUMBER_CPF - value;
  }
  
  public static String createNumberCpf() {
    CPFUtils utils = new CPFUtils();
    List<Integer> numbersCpf = utils.doGenerate();
    final String stringCpf = numbersCpf.stream().map(i -> Integer.toString(i))
        .reduce(SPLIT_CHARACTER, String::concat);
    return stringCpf;
  }
  
  public static boolean isValid(final String numberCpf) {
    CPFUtils utils = new CPFUtils();
    Objects.requireNonNull(numberCpf);
    if ((numberCpf.isEmpty()) ||
        (numberCpf.length() != CAPACITY_MAX_NUMBER_CPF)) {
      return false;
    }
    
    List<Integer> listNumbersCpf = Stream.of(numberCpf.split(SPLIT_CHARACTER))
        .mapToInt(Integer::new)
        .boxed()
        .collect(Collectors.toList());
    
    int firstDigit = listNumbersCpf.get(INDEX_FIRST_DIGIT_VERIFIER);
    int secondDigit = listNumbersCpf.get(INDEX_SECOND_DIGIT_VERIFIER);
    
    int firstDigitCalculate = utils
        .calculateDigitVerifier(listNumbersCpf.subList(ZERO, INDEX_FIRST_DIGIT_VERIFIER));
    
    if (firstDigit != firstDigitCalculate) {
      return false;
    }
    
    int secondDigitCalculate = utils
        .calculateDigitVerifier(listNumbersCpf.subList(ZERO, INDEX_SECOND_DIGIT_VERIFIER));
    if (secondDigit != secondDigitCalculate) {
      return false;
    }
    
    return true;
  }
}
