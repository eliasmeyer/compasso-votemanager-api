package br.com.compasso.votacao.api.service.verifier;

import static br.com.compasso.votacao.api.helper.TestHelper.createSession;
import static br.com.compasso.votacao.api.helper.TestHelper.createVote;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import br.com.compasso.votacao.api.enums.OptionVotation;
import br.com.compasso.votacao.api.exception.VoteAlreadyRegisteredException;
import br.com.compasso.votacao.api.model.Session;
import br.com.compasso.votacao.api.model.Vote;
import br.com.compasso.votacao.api.service.VoteService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifierVoteRegisteredTest {
  
  @Mock
  private VoteService voteService;
  @InjectMocks
  private VerifierVoteRegistered verifierVoteRegistered;
  
  @Test
  void testShouldValidateSuccessfully() {
    //given
    given(voteService.findBySessionAndCpfNumber(any(Session.class), eq("12345678901")))
        .willReturn(Optional.empty());
    
    //when
    verifierVoteRegistered.isOk(new VoteVerifierBean(createSession(1l, 1L), "12345678901"));
    
    //then
    then(voteService).should().findBySessionAndCpfNumber(any(Session.class), eq("12345678901"));
  }
  
  @Test
  void testShouldThrowVoteAlreadyRegisteredException() {
    //given
    Vote vote = createVote(createSession(1L, 1L), "12345678901", OptionVotation.SIM);
    given(voteService.findBySessionAndCpfNumber(any(Session.class), eq("12345678901")))
        .willReturn(Optional.of(vote));
    
    //when + then
    assertThatExceptionOfType(VoteAlreadyRegisteredException.class)
        .isThrownBy(() -> {
          verifierVoteRegistered.isOk(new VoteVerifierBean(createSession(1L, 1L), "12345678901"));
        });
  }
}
