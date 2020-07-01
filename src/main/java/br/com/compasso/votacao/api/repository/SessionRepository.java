package br.com.compasso.votacao.api.repository;

import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.model.Session;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface SessionRepository extends JpaRepository<Session, Long> {
  
  @Query("select s from Session s where s.dateTimeClosing <= :dateTimeCurrent "
      + "and s.statusSession = br.com.compasso.votacao.api.enums.StatusSession.ABERTO")
  List<Session> findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(
      @Param("dateTimeCurrent") LocalDateTime dateTimeCurrent);
  
  List<Session> findAllByStatusSession(StatusSession statusSession);
}
