package br.com.compasso.votacao.api.config.scheduler;


import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

import br.com.compasso.votacao.api.service.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {
  
  @Mock
  private SessionService sessionService;
  @InjectMocks
  private SchedulerConfig schedulerConfig;
  
  @Test
  public void testShouldRunSessionServiceClose() {
    //given
    ReflectionTestUtils.setField(schedulerConfig, "isEnabled", Boolean.TRUE);
    willDoNothing().given(sessionService).close();
    
    //when
    schedulerConfig.scheduledJob();
    
    //then
    then(sessionService).should(times(1)).close();
  }
}
