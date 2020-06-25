package br.com.compasso.votacao.api.mapper;

import br.com.compasso.votacao.api.adapter.SessionResponse;
import br.com.compasso.votacao.api.model.Session;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SessionMapper {
  
  @Mappings({
      @Mapping(target = "topicId", source = "topic.id"),
      @Mapping(target = "dateTimeOpening", source = "dateTimeOpening", dateFormat = "yyyy-MM-dd HH:mm:ss"),
      @Mapping(target = "dateTimeClosing", source = "dateTimeClosing", dateFormat = "yyyy-MM-dd HH:mm:ss")
  })
  SessionResponse from(Session session);
  
  List<SessionResponse> from(List<Session> session);
  
}
