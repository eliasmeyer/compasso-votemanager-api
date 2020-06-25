package br.com.compasso.votacao.api.model;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Pauta")
public class Topic {
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_generator")
  @SequenceGenerator(name = "topic_generator", sequenceName = "pauta_id_seq", allocationSize = 1)
  @Column(updatable = false, nullable = false)
  private Long id;
  @Column(name = "assunto", length = 70, nullable = false)
  private String title;
  @Column(name = "descricao", length = 300)
  private String description;
  @CreatedDate
  @Column(name = "criado_em", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "resultado_id")
  private Result result;
  
}
