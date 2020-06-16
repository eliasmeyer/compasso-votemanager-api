package br.com.compasso.votacao.api.adapters;

import br.com.compasso.votacao.api.controllers.validation.EnunNamePattern;
import br.com.compasso.votacao.api.enums.OptionVotation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {
  
  @Positive
  @NotNull(message = "Id Associate is null")
  private Long idAssociate;
  @NotNull(message = "Option Vote is null")
  @EnunNamePattern(regexp = "SIM|NAO")
  private OptionVotation choiceOfVote;
}
