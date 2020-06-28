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
  
  @NotNull(message = "Cpf can't be null")
  @NotBlank(message = "Cpf can't be blank")
  private String cpf;
  @NotNull(message = "Vote can't be null")
  @EnunNamePattern(regexp = "SIM|NAO", message = "Value should be SIM or NAO")
  private OptionVotation vote;
}
