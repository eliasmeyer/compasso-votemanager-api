package br.com.compasso.votacao.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.compasso.votacao.api.config.jpa.JpaConfig;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Vote;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
@Sql(value = {"/sql/data.sql"})
public class VoteRepositoryIT {
  
  @Autowired
  private VoteRepository voteRepository;
  @Autowired
  private SessionRepository sessionRepository;
  
  @Test
  @DisplayName("Find all by session")
  public void testShouldFindAllBySession() {
    //given
    Vote voteOne = voteRepository.findAll().stream().findFirst().get();
    
    //when
    List<Vote> votesBySession = voteRepository.findAllBySession(voteOne.getSession());
    
    //then
    votesBySession.forEach(vote -> {
      assertThat(vote.getId()).isNotNull();
      assertThat(vote.getSession()).isNotNull();
      assertThat(vote.getCpfNumber()).isNotNull();
      assertThat(vote.getDateTimeVote()).isNotNull();
      assertThat(vote.getOptionVotation()).isNotNull();
    });
  }
  
  @Test
  @DisplayName("Find all by Session and Cpf number")
  public void testShouldFindBySessionAndCpfNumber() {
    //given
    Vote voteOne = voteRepository.findAll().stream().findFirst().get();
    
    //when
    Vote vote = voteRepository
        .findBySessionAndCpfNumber(voteOne.getSession(), voteOne.getCpfNumber()).get();
    
    //then
    assertThat(vote.getId()).isNotNull();
    assertThat(vote.getOptionVotation()).isNotNull();
    assertThat(vote.getCpfNumber()).isNotNull();
    assertThat(vote.getSession()).isNotNull();
    assertThat(vote.getDateTimeVote()).isNotNull();
  }
  
  @Test
  @DisplayName("Count votes by session")
  public void testShouldCountVoteBySession() {
    //given
    final Long expectedTotalVotes = 3L;
    Session sessionOne = sessionRepository.findAll().stream().findFirst().get();
    
    //when
    List<Result> results = voteRepository.countBySession(sessionOne);
    
    //then
    assertThat(results).isNotNull().isNotEmpty();
    assertThat(results).hasAtLeastOneElementOfType(Result.class);
    Result result = results.stream().findFirst().get();
    assertThat(result.getTotalVotes()).isEqualTo(expectedTotalVotes);
  }
}
