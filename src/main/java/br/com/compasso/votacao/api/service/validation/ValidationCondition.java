package br.com.compasso.votacao.api.service.validation;

public interface ValidationCondition<T> {
  
  void isOk(T t) throws Exception;
}
