package br.com.compasso.votacao.api.repositories;

import br.com.compasso.votacao.api.models.Topic;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "topics", path = "topics")
public interface TopicRepository extends PagingAndSortingRepository<Topic, Long> {
  
}
