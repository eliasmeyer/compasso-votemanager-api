package br.com.compasso.votacao.api.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CpfResponse {
  
  private String cpfNumber;
  private Boolean isValid;
}
