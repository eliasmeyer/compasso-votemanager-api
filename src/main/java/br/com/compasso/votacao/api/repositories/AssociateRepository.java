package br.com.compasso.votacao.api.repositories;

import br.com.compasso.votacao.api.models.Associate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "associates", path = "associates")
public interface AssociateRepository extends PagingAndSortingRepository<Associate, Long> {
  
}
