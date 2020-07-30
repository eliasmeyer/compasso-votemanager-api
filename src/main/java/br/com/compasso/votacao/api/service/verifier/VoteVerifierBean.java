package br.com.compasso.votacao.api.service.verifier;

import br.com.compasso.votacao.api.model.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VoteVerifierBean {
  
  private final Session session;
  private final String cpfNumber;
}
