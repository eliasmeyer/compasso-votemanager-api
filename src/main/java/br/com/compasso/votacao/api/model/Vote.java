package br.com.compasso.votacao.api.model;

import br.com.compasso.votacao.api.enums.OptionVotation;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Voto")
public class Vote implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_generator")
  @SequenceGenerator(name = "vote_generator", sequenceName = "voto_id_seq", allocationSize = 1)
  @Column(updatable = false, nullable = false)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "sessao_id", nullable = false)
  private Session session;
  @Column(name = "cpf_number", length = 11, unique = true, nullable = false)
  private String cpfNumber;
  @Enumerated(EnumType.STRING)
  @Type(type = "br.com.compasso.votacao.api.enums.EnumTypePostgreSQL")
  @Column(name = "opcao")
  private OptionVotation optionVotation;
  @CreatedDate
  @Column(name = "data_hora_voto", columnDefinition = "TIMESTAMP")
  private LocalDateTime dateTimeVote;
  
  
}
