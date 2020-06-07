package br.com.compasso.votacao.api.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@Table(name = "Associado")
public class Associate {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nome", length = 70, unique = true, nullable = false)
  private String name;
  @CreatedDate
  @Column(name = "criadoEm", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;
}
