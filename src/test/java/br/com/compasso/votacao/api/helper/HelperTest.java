package br.com.compasso.votacao.api.helper;

import br.com.compasso.votacao.api.adapter.TopicRequest;
import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.utils.CPFUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class Helper for test
 */
public class HelperTest {
  
  ThreadLocalRandom random = ThreadLocalRandom.current();
  
  public static String generateCpf() {
    return CPFUtils.createNumberCpf();
  }
  
  public static TopicRequest createTopicRequest(String title, String description) {
    TopicRequest topicRequest = new TopicRequest();
    topicRequest.setTitle(title);
    topicRequest.setDescription(description);
    return topicRequest;
  }
  
  public static Topic createTopic(Long id, String title, String description) {
    Topic topic = new Topic();
    topic.setId(id);
    topic.setTitle(title);
    topic.setDescription(description);
    topic.setCreatedAt(LocalDateTime.now());
    return topic;
  }
  
  public static Result createResult(Long id, OptionVotation electedOption, Long totalVotes) {
    Result result = new Result();
    result.setId(id);
    result.setElectedOption(electedOption);
    result.setTotalVotes(totalVotes);
    return result;
  }
  
  public static Session createSession(Long id, Long votationTime) {
    LocalDateTime localDateTime = LocalDateTime.now();
    Session session = new Session();
    session.setId(id);
    session.setDateTimeOpening(localDateTime);
    session.setDateTimeClosing(localDateTime.plusMinutes(Optional.of(votationTime).orElse(1L)));
    session.setStatusSession(StatusSession.ABERTO);
    return session;
  }
  
  public static Vote createVote(Session session, String cpfNumber,
      OptionVotation optionVotation) {
    Vote vote = new Vote();
    vote.setSession(session);
    vote.setCpfNumber(cpfNumber);
    vote.setOptionVotation(optionVotation);
    vote.setDateTimeVote(LocalDateTime.now());
    return vote;
  }
}
