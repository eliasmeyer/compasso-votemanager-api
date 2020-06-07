package br.com.compasso.votacao.api.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@Table
public class Associate {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 70, unique = true, nullable = false)
  private String name;
  @CreatedDate
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;
}
