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
  
  @Positive(message = "topicId should be positive number")
  @NotNull(message = "TopicId can't be null")
  private Long topicId;
  @Min(value = 1L, message = "Value minuteTimeVoting should be greater than 1")
  private Long minuteTimeVoting;
  
  
}
