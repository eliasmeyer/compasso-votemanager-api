package br.com.compasso.votacao.api.model;

import br.com.compasso.votacao.api.enums.OptionVotation;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Resultado")
public class Result implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructor to groupBy JPA
   */
  public Result(OptionVotation electedOption, Long totalVotes) {
    this.electedOption = electedOption;
    this.totalVotes = totalVotes;
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_generator")
  @SequenceGenerator(name = "result_generator", sequenceName = "resultado_id_seq", allocationSize = 1)
  @Column(updatable = false, nullable = false)
  private Long id;
  @Column(name = "opcao_eleita")
  @Type(type = "br.com.compasso.votacao.api.enums.EnumTypePostgreSQL")
  @Enumerated(EnumType.STRING)
  private OptionVotation electedOption;
  @Column(name = "total")
  private Long totalVotes;
}
