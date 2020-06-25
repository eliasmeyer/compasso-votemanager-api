package br.com.compasso.votacao.api.repository;

import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Vote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface VoteRepository extends JpaRepository<Vote, Long> {
  
  List<Vote> findAllBySession(Session session);
  
  Optional<Vote> findBySessionAndCpfNumber(Session sessio, String cpfNumber);
  
  @Query("select new br.com.compasso.votacao.api.model.Result(v.optionVotation, count(1))"
      + " from Vote v where v.session = :session group by v.optionVotation order by count(1) desc")
  List<Result> countBySession(@Param("session") Session session);
  
}
