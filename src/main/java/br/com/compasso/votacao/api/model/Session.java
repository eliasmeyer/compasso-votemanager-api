package br.com.compasso.votacao.api.model;

import br.com.compasso.votacao.api.enums.StatusSession;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Sessao")
public class Session {
  
  @Id
  private Long id;
  @CreatedDate
  @Column(name = "data_hora_abertura", columnDefinition = "TIMESTAMP")
  private LocalDateTime dateTimeOpening;
  @Column(name = "data_hora_fechamento", columnDefinition = "TIMESTAMP")
  private LocalDateTime dateTimeClosing;
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Type(type = "br.com.compasso.votacao.api.enums.EnumTypePostgreSQL")
  private StatusSession statusSession;
  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "id")
  private Topic topic;
  
}
