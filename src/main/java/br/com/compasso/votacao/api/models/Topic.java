package br.com.compasso.votacao.api.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Pauta")
public class Topic {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "assunto", length = 70, nullable = false)
  private String title;
  @Column(name = "descricao", length = 300, nullable = false)
  private String description;
  @CreatedDate
  @Column(name = "criadoEm", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_Resultado", referencedColumnName = "id")
  private Result result;
  
}
