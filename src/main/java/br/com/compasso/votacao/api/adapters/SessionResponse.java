package br.com.compasso.votacao.api.adapters;

import br.com.compasso.votacao.api.enums.StatusSession;
import java.time.LocalDateTime;
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
  private LocalDateTime dateTimeOpening;
  private LocalDateTime dateTimeClosing;
  private StatusSession statusSession;
  private Long idTopic;
}
