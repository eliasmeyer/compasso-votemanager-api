package br.com.compasso.votacao.api.adapters;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
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
public class SessionRequest {
  
  @Positive
  @NotEmpty(message = "Favor fornecer codigo da pauta")
  private Long idTopic;
  @Positive
  @Min(value = 1L, message = "Valor minimo inferior a 1 minuto")
  private Long minuteTimeVoting;
  
  
}
