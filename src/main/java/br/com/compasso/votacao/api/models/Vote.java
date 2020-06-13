package br.com.compasso.votacao.api.models;

import br.com.compasso.votacao.api.enums.OptionVotation;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Data
@NoArgsConstructor
@Entity
@Table
public class Vote {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_Sessao", nullable = false)
  private Session session;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_Associado", nullable = false)
  private Associate associate;
  @Enumerated
  @Column(name = "opcao")
  private OptionVotation optionVotation;
  @CreatedDate
  @Column(name = "dataHoraVoto", columnDefinition = "TIMESTAMP")
  private LocalDateTime dateTimeVote;
  
  
}
