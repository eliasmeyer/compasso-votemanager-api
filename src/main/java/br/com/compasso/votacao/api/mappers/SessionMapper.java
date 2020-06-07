package br.com.compasso.votacao.api.mappers;

import br.com.compasso.votacao.api.adapters.SessionResponse;
import br.com.compasso.votacao.api.models.Session;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SessionMapper {
  
  SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);
  
  @Mapping(source = "topic.id", target = "idTopic")
  SessionResponse toSessionResponse(Session session);
  
  List<SessionResponse> toSessionResponses(List<Session> session);
}
