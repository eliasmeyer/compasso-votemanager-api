package br.com.compasso.votacao.api.helper;

import br.com.compasso.votacao.api.adapter.SessionRequest;
import br.com.compasso.votacao.api.adapter.TopicRequest;
import br.com.compasso.votacao.api.adapter.TopicResponse;
import br.com.compasso.votacao.api.adapter.VoteRequest;
import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.enums.StatusSession;
import br.com.compasso.votacao.api.model.Result;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Topic;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.utils.CPFUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

/**
 * Class Helper for test
 */
public class TestHelper {
  
  public static String generateCpf() {
    return CPFUtils.createNumberCpf();
  }
  
  public static List<Session> makeListSessions(int amount, StatusSession statusSession) {
    List<Session> sessions = new ArrayList<>(amount);
    for (long i = 0; i < amount; i++) {
      Session session = createSession(i, 1L);
      Topic topic = createTopic(i, "Test " + i, "Description Test");
      session.setTopic(topic);
      session.setStatusSession(statusSession);
      sessions.add(session);
    }
    return sessions;
  }
  
  public static List<Topic> makeListTopics(int amount) {
    List<Topic> topics = new ArrayList<>(amount);
    for (long i = 0; i < amount; i++) {
      Topic topic = createTopic(i, "Test " + i, "Description Test");
      topics.add(topic);
    }
    return topics;
  }
  
  public static TopicRequest createTopicRequest(String title, String description) {
    TopicRequest topicRequest = new TopicRequest();
    topicRequest.setTitle(title);
    topicRequest.setDescription(description);
    return topicRequest;
  }
  
  public static Topic createTopic(String title, String description) {
    Topic topic = new Topic();
    topic.setTitle(title);
    topic.setDescription(description);
    return topic;
  }
  
  public static TopicResponse createTopicResponse(String title, String description) {
    TopicResponse topicResponse = new TopicResponse();
    topicResponse.setId(1L);
    topicResponse.setTitle(title);
    topicResponse.setDescription(description);
    topicResponse.setResult(null);
    return topicResponse;
  }
  
  public static Topic createTopic(Long id, String title, String description) {
    Topic topic = new Topic();
    topic.setId(id);
    topic.setTitle(title);
    topic.setDescription(description);
    topic.setCreatedAt(LocalDateTime.now());
    return topic;
  }
  
  public static List<Result> createResultList() {
    List<Result> results = new ArrayList<>(2);
    results.add(createResult(1L, OptionVotation.NAO, 20L));
    results.add(createResult(2L, OptionVotation.SIM, 19L));
    return results;
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
  
  public static SessionRequest createSessionRequest(Long topicId, Long timeForVoting) {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setTopicId(topicId);
    sessionRequest.setMinuteTimeVoting(timeForVoting);
    return sessionRequest;
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
  
  public static VoteRequest createVoteRequest(String cpfNumber, OptionVotation choice) {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setCpf(cpfNumber);
    voteRequest.setVote(choice);
    return voteRequest;
  }
  
  public static CustomComparator ignoreFields(String... ignoreFields) {
    
    List<Customization> customizations = new ArrayList<>();
    Stream<String> stringStream = Arrays.stream(ignoreFields);
    stringStream.forEach(x ->
        customizations.add(new Customization(x, ((o1, o2) -> true)))
    );
    
    return new CustomComparator(JSONCompareMode.LENIENT,
        customizations.toArray(new Customization[customizations.size()]));
  }
  
  public static ResponseBodyMatchers responseBody() {
    return new ResponseBodyMatchers();
  }
}
