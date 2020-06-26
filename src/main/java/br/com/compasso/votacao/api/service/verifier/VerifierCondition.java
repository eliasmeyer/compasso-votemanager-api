package br.com.compasso.votacao.api.service.verifier;

public interface VerifierCondition<T> {
  
  void isOk(T t) throws Exception;
}
