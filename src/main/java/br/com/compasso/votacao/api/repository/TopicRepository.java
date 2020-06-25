package br.com.compasso.votacao.api.repository;

import br.com.compasso.votacao.api.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface TopicRepository extends JpaRepository<Topic, Long> {
  
}
