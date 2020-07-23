package br.com.compasso.votacao.api.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ResultPublisher {
  
  String OUTPUT = "compasso-channel";
  
  @Output(OUTPUT)
  MessageChannel output();
  
}
