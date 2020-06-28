package br.com.compasso.votacao.api.adapter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class TopicRequest {
  
  @NotNull(message = "Title can't be null")
  @Size(min = 3, max = 70)
  private String title;
  private String description;
}
