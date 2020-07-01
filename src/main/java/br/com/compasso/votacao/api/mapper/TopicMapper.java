package br.com.compasso.votacao.api.mapper;

import br.com.compasso.votacao.api.adapter.TopicResponse;
import br.com.compasso.votacao.api.model.Topic;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopicMapper {
  
  TopicResponse from(Topic topic);
  
  List<TopicResponse> from(List<Topic> session);
  
}
