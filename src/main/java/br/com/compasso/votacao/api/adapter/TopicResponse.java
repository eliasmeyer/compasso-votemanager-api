package br.com.compasso.votacao.api.adapter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TopicResponse {
  
  private Long id;
  private String title;
  private String description;
  private String createdAt;
  private ResultResponse result;
  
}
