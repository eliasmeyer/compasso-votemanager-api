package br.com.compasso.votacao.api.mapper;

import br.com.compasso.votacao.api.adapter.VoteResponse;
import br.com.compasso.votacao.api.model.Vote;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VoteMapper {
  
  @Mappings({
      @Mapping(target = "sessionId", source = "session.id"),
      @Mapping(target = "cpf", source = "cpfNumber"),
      @Mapping(target = "vote", source = "optionVotation"),
      //@Mapping(target = "dateTimeVote", source = "dateTimeVote", dateFormat = "yyyy-MM-dd HH:mm:ss")
  })
  VoteResponse from(Vote vote);
  
  List<VoteResponse> from(List<Vote> vote);
}
