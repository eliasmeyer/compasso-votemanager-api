package br.com.compasso.votacao.api.service.verifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import br.com.compasso.votacao.api.helper.TestHelper;
import br.com.compasso.votacao.api.model.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifierOnSessionTest {
  
  @Mock
  VerifierSessionExpired verifierSessionExpired;
  @Mock
  VerifierSessionIsOpen verifierSessionIsOpen;
  @Mock
  VerifierAssociateIsAble verifierAssociateIsAble;
  @Mock
  VerifierVoteRegistered verifierVoteRegistered;
  @InjectMocks
  VerifierOnSession verifierOnSession;
  
  @Test
  void testShouldValidateSuccessfully() {
    //given
    Session session = TestHelper.createSession(1L, 1L);
    String cpfNumber = "12345678901";
    willDoNothing().given(verifierSessionExpired).isOk(session);
    willDoNothing().given(verifierSessionIsOpen).isOk(session);
    willDoNothing().given(verifierAssociateIsAble).isOk("12345678901");
    willDoNothing().given(verifierVoteRegistered).isOk(any(VoteVerifierBean.class));
    
    //when
    verifierOnSession.validate(session, cpfNumber);
    
    //then
    then(verifierSessionExpired).should().isOk(eq(session));
    then(verifierSessionIsOpen).should().isOk(eq(session));
    then(verifierAssociateIsAble).should().isOk(eq(cpfNumber));
    then(verifierVoteRegistered).should().isOk(any(VoteVerifierBean.class));
  }
}
