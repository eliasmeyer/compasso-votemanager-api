package br.com.compasso.votacao.api.repositories;

import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.models.Session;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
  
  @Query("select s from Session s where s.dateTimeClosing <= :dateTimeClosing and s.statusSession = 'ABERTO'")
  List<Session> findAllWithDateTimeClosingAndStatusSessionOpen(
      @Param("dateTimeClosing") LocalDateTime dateTimeClosing);
  
  List<Session> findAllByStatusSession(@Param("statusSession") StatusSession statusSession);
}
