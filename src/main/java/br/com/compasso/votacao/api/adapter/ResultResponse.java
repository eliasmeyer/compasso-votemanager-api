package br.com.compasso.votacao.api.adapter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultResponse {
  
  private String electedOption;
  private Long totalVotes;
  
}
