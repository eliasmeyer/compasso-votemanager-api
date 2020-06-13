package br.com.compasso.votacao.api.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.models.Topic;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TopicRepositoryIntegrationTest {
  
  private TopicRepository topicRepository;
  private EntityManager entityManager;
  
  @Autowired
  TopicRepositoryIntegrationTest(EntityManager entityManager, TopicRepository topicRepository) {
    this.entityManager = entityManager;
    this.topicRepository = topicRepository;
  }
  
  @Test
  public void testFindAllOk() {
    //Given
    Topic topic = new Topic();
    topic.setTitle("TEST #1 FIND ALL OK");
    topic.setDescription("Checking #1 test find all");
    
    entityManager.persist(topic);
    entityManager.flush();
    
    //Given
    Topic topic2 = new Topic();
    topic2.setTitle("TEST #2 FIND ALL OK");
    topic2.setDescription("Checking test #2 find all");
    
    entityManager.persist(topic);
    entityManager.persist(topic2);
    entityManager.flush();
    
    //when
    Iterable<Topic> topics = topicRepository.findAll();
    assertThat(topics).hasSize(2);
  }
  
  @Test
  public void testFindByIdOk() {
    
  }
  
  @Test
  public void testFindByIdNotFound() {
    
  }
  
  
}
