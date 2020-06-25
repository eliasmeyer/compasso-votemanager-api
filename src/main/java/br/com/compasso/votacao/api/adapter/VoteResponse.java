package br.com.compasso.votacao.api.adapter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VoteResponse {
  
  private Long sessionId;
  private String cpf;
  private String vote;
  private String dateTimeVote;
}
