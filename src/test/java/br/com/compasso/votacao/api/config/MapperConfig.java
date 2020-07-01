package br.com.compasso.votacao.api.config;

import br.com.compasso.votacao.api.mapper.SessionMapper;
import br.com.compasso.votacao.api.mapper.SessionMapperImpl;
import br.com.compasso.votacao.api.mapper.TopicMapper;
import br.com.compasso.votacao.api.mapper.TopicMapperImpl;
import br.com.compasso.votacao.api.mapper.VoteMapper;
import br.com.compasso.votacao.api.mapper.VoteMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
  
  @Bean
  public TopicMapper topicMapper() {
    return new TopicMapperImpl();
  }
  
  @Bean
  public SessionMapper sessionMapper() {
    return new SessionMapperImpl();
  }
  
  @Bean
  public VoteMapper voteMapper() {
    return new VoteMapperImpl();
  }
}
