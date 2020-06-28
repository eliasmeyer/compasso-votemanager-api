package br.com.compasso.votacao.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TopicRepositoryIT {
  
  @Autowired
  private TopicRepository topicRepository;
  
  @Test
  @DisplayName("Find by id successfully")
  public void testShouldFindByIdSuccessfully() {
    //given
    Topic topic = new Topic();
    topic.setTitle("Pauta Test #2");
    topic.setDescription("Pauta Test Description #2");
    
    Topic inserted = topicRepository.save(topic);
    
    //when
    Topic found = topicRepository.findById(inserted.getId()).get();
    
    //then
    assertThat(found.getId()).isEqualTo(inserted.getId());
    assertThat(found.getTitle()).isEqualTo(inserted.getTitle());
    assertThat(found.getDescription()).isEqualTo(inserted.getDescription());
    assertThat(found.getCreatedAt()).isEqualTo(inserted.getCreatedAt());
  }
}
