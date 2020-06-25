package br.com.compasso.votacao.api.adapter;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
  @NotNull(message = "Topic ID is null")
  private Long topicId;
  @Positive
  @Min(value = 1L, message = "Value minuteTimeVoting is invalid")
  private Long minuteTimeVoting;
  
  
}
