package br.com.compasso.votacao.api.service.verifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import br.com.compasso.votacao.api.exception.VotingTimeSessionExpiredException;
import br.com.compasso.votacao.api.helper.TestHelper;
import br.com.compasso.votacao.api.model.Session;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VerifierSessionExpiredTest {
  
  VerifierSessionExpired verifierSessionExpired = new VerifierSessionExpired();
  
  @Test
  @DisplayName("Session not expired")
  void testShouldOk() {
    Session session = TestHelper.createSession(1L, 1L);
    LocalDateTime currentDateTime = LocalDateTime.now().plusSeconds(3l);
    
    try {
      verifierSessionExpired.isOk(session);
    } catch (VotingTimeSessionExpiredException e) {
      fail("Should'nt throw VotingTimeSessionExpiredException");
    }
    
    assertThat(currentDateTime.isAfter(session.getDateTimeOpening()) &&
        currentDateTime.isBefore(session.getDateTimeClosing())).isTrue();
  }
  
  @ParameterizedTest
  @DisplayName("Session expired")
  @MethodSource("providesDateTimeForExpired")
  void testShouldntOk(LocalDateTime dateTimeOpen, LocalDateTime dateTimeClose) {
    Session session = TestHelper.createSession(1L, 1L);
    LocalDateTime currentDateTime = LocalDateTime.now();
    session.setDateTimeOpening(dateTimeOpen);
    session.setDateTimeClosing(dateTimeClose);
    
    assertThatExceptionOfType(VotingTimeSessionExpiredException.class)
        .isThrownBy(() -> {
          verifierSessionExpired.isOk(session);
        });
    
    assertThat(currentDateTime.isAfter(session.getDateTimeOpening()) &&
        currentDateTime.isBefore(session.getDateTimeClosing())).isFalse();
  }
  
  private static Stream<Arguments> providesDateTimeForExpired() {
    LocalDateTime localDateTime = LocalDateTime.now();
    return Stream.of(
        Arguments.of(localDateTime.minusMinutes(59L), localDateTime.minusMinutes(50L)),
        Arguments.of(localDateTime.plusMinutes(50L), localDateTime.plusMinutes(59L)),
        Arguments.of(localDateTime.plusHours(5), localDateTime.plusHours(6)),
        Arguments.of(localDateTime.plusDays(1), localDateTime.plusDays(2)),
        Arguments.of(localDateTime.plusMonths(1), localDateTime.plusMonths(2)),
        Arguments.of(localDateTime.minusHours(2), localDateTime.minusHours(1))
    );
  }
}
