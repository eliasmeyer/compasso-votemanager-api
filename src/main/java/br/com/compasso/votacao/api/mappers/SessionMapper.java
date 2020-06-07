package br.com.compasso.votacao.api.mappers;

import br.com.compasso.votacao.api.adapters.SessionResponse;
import br.com.compasso.votacao.api.models.Session;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {
  
  @Mapping(target = "idTopic", source = "topic.id")
  SessionResponse from(Session session);
  
  List<SessionResponse> from(List<Session> session);
  
}
