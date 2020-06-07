package br.com.compasso.votacao.api.repositories;

import br.com.compasso.votacao.api.models.Associate;
import br.com.compasso.votacao.api.models.Session;
import br.com.compasso.votacao.api.models.Vote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
  
  @Query("select v from Vote v where v.session.id = :idSession")
  List<Vote> findAllByIdSession(@Param("idSession") Long idSession);
  
  @Query("select v from Vote v where v.session.id = :idSession and v.associate.id = :idAssociate")
  Optional<Vote> findBySessionAndAssociate(Session session, Associate associate);
  
}
