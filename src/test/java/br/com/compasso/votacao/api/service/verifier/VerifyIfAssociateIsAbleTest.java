package br.com.compasso.votacao.api.service.verifier;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import br.com.compasso.votacao.api.exception.AssociateUnableForVotingException;
import br.com.compasso.votacao.api.service.external.AssociateResponse;
import br.com.compasso.votacao.api.service.external.AssociateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifyIfAssociateIsAbleTest {
  
  @Mock
  private AssociateService associateService;
  
  @InjectMocks
  private VerifyIfAssociateIsAble verifyIfAssociateIsAble;
  
  @Test
  @DisplayName("Associate is ABLE_TO_VOTE")
  void testShouldBeOk() {
    //given
    AssociateResponse associateResponse = new AssociateResponse("ABLE_TO_VOTE");
    given(associateService.isAbleToVote(anyString())).willReturn(associateResponse);
    
    verifyIfAssociateIsAble.isOk("12345678901");
    
    then(associateService).should().isAbleToVote(eq("12345678901"));
  }
  
  @Test
  @DisplayName("Associate is UNABLE_TO_VOTE")
  void testShouldntBeOk() {
    //given
    AssociateResponse associateResponse = new AssociateResponse("UNABLE_TO_VOTE");
    given(associateService.isAbleToVote(anyString())).willReturn(associateResponse);
    
    assertThatExceptionOfType(AssociateUnableForVotingException.class)
        .isThrownBy(() -> {
          verifyIfAssociateIsAble.isOk("12345678901");
        });
  }
}
