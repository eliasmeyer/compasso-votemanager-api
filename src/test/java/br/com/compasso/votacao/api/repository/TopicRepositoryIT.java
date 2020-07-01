package br.com.compasso.votacao.api.repository;

import static br.com.compasso.votacao.api.helper.HelperTest.createTopic;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.config.jpa.JpaConfig;
import br.com.compasso.votacao.api.model.Topic;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class TopicRepositoryIT {
  
  @Autowired
  private TopicRepository topicRepository;
  
  @Test
  @DisplayName("Find by id successfully")
  public void testShouldFindByIdSuccessfully() {
    //given
    Topic topic = createTopic("Pauta Test #2", "Pauta Test Description #2");
    Topic inserted = topicRepository.save(topic);
  
    //when
    Topic found = topicRepository.findById(inserted.getId()).get();
  
    //then
    assertThat(found.getId()).isEqualTo(inserted.getId());
    assertThat(found.getTitle()).isEqualTo(inserted.getTitle());
    assertThat(found.getDescription()).isEqualTo(inserted.getDescription());
    assertThat(found.getCreatedAt()).isEqualTo(inserted.getCreatedAt());
  }
  
  @Test
  @DisplayName("Find by title ignore case")
  public void testShouldFindTitleEqualsSuccessfully() {
    Topic topic1 = createTopic("TiTlE #1", "Description #1");
    topicRepository.save(topic1);
    
    //when
    Optional<Topic> found = topicRepository.findByTitleIgnoreCase("title #1");
    
    //then
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getTitle()).describedAs("title").isEqualTo("TiTlE #1");
    assertThat(found.get().getDescription()).describedAs("description").isEqualTo("Description #1");
  }
}
