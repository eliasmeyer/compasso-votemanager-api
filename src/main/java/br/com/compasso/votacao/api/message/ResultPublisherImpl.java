package br.com.compasso.votacao.api.message;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(ResultPublisher.class)
public class ResultPublisherImpl {
  
}
