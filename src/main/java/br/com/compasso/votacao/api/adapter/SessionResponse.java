package br.com.compasso.votacao.api.adapter;

import br.com.compasso.votacao.api.enums.StatusSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
  
  private Long id;
  private String dateTimeOpening;
  private String dateTimeClosing;
  private StatusSession statusSession;
  private Long topicId;
}
