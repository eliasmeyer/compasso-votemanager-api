package br.com.compasso.votacao.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.config.jpa.JpaConfig;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
@Sql(value = {"/sql/data.sql"})
public class SessionRepositoryIT {
  
  @Autowired
  private SessionRepository sessionRepository;
  @Autowired
  private TopicRepository topicRepository;
  
  @Test
  @DisplayName("Find all sessions that are OPEN and time session no expired")
  public void testShouldFindAllThatPrecedesDateTimeClosingAndStatusEqualOpen() {
    // given
    Topic topic = new Topic();
    topic.setTitle("Topic test - Query Sessions");
    topic.setDescription("Query findAllThatPrecedesDateTimeClosingAndStatusEqualOpen");
    topic = topicRepository.save(topic);
    
    Session session = new Session();
    LocalDateTime localDateTime = LocalDateTime.now();
    session.setTopic(topic);
    session.setDateTimeOpening(localDateTime.withNano(0));
    session.setDateTimeClosing(localDateTime.plusMinutes(2L).withNano(0));
    session.setStatusSession(StatusSession.ABERTO);
    
    Session inserted = sessionRepository.save(session);
//    sessionRepository.flush();
    
    //when
    List<Session> founds = sessionRepository
        .findAllThatPrecedesDateTimeClosingAndStatusEqualOpen(LocalDateTime.now());
    
    //then
    assertThat(founds).hasAtLeastOneElementOfType(Session.class);
    for (Session currentSession : founds) {
      assertThat(currentSession.getDateTimeOpening()).isEqualTo(inserted.getDateTimeOpening());
      assertThat(currentSession.getStatusSession()).isEqualTo(StatusSession.ABERTO);
      assertThat(currentSession.getDateTimeClosing()).isAfter(localDateTime);
    }
  }
  
  @Test
  @DisplayName("Find all by status session")
  public void testShouldFindAllByStatusSession() {
    //given
    
    //when
    List<Session> sessions = sessionRepository.findAllByStatusSession(StatusSession.FECHADO);
    
    //then
    assertThat(sessions).hasSize(1);
  }
  
}
