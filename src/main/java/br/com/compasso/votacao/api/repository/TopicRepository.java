package br.com.compasso.votacao.api.repository;

import br.com.compasso.votacao.api.model.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface TopicRepository extends JpaRepository<Topic, Long> {
  
  Optional<Topic> findByTitleIgnoreCase(String title);
  
  @EntityGraph(attributePaths = "result", type = EntityGraphType.LOAD)
  Optional<Topic> findOneWithResultById(Long id);
  
  @Override
  @EntityGraph(attributePaths = "result", type = EntityGraphType.LOAD)
  List<Topic> findAll();
  
}
