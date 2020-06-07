package br.com.compasso.votacao.api.models;

import br.com.compasso.votacao.api.enums.OptionVotation;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Resultado")
public class Result {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "opcaoEleita")
  @Enumerated(EnumType.STRING)
  private OptionVotation electedOption;
  @Column(name = "total")
  private Long totalVotes;
}
