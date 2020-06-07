package br.com.compasso.votacao.api.models;

import br.com.compasso.votacao.api.enums.StatusSession;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Data
@NoArgsConstructor
@Entity
@Table
public class Session {
  
  @Id
  private Long id;
  @CreatedDate
  @Column(name = "dataHoraAbertura", columnDefinition = "TIMESTAMP")
  private LocalDateTime dateTimeOpening;
  @Column(name = "dataHoraFechamento", columnDefinition = "TIMESTAMP")
  private LocalDateTime dateTimeClosing;
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private StatusSession statusSession;
  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  private Topic topic;
  
}
