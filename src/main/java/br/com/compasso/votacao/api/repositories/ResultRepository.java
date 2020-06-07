package br.com.compasso.votacao.api.repositories;

import br.com.compasso.votacao.api.models.Result;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends CrudRepository<Result, Long> {
  
}
