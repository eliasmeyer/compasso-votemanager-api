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
@Table
public class Topic {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 70, nullable = false)
  private String title;
  @Column(length = 300, nullable = false)
  private String description;
  @CreatedDate
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_result", referencedColumnName = "id")
  private Result result;
  
}
