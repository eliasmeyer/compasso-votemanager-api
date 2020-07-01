package br.com.compasso.votacao.api.adapter;

import br.com.compasso.votacao.api.controller.validation.EnunNamePattern;
import br.com.compasso.votacao.api.enums.OptionVotation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class VoteRequest {
  
  @NotBlank(message = "Cpf can't be blank or null")
  private String cpf;
  @NotNull(message = "Vote can't be null")
  @EnunNamePattern(regexp = "SIM|NAO", message = "Vote should be SIM or NAO")
  private OptionVotation vote;
}
