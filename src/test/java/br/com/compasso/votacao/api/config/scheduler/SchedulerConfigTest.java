package br.com.compasso.votacao.api.config.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SchedulerConfigTest {
  
  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withInitializer(
          new ConditionEvaluationReportLoggingListener())
      .withUserConfiguration(SchedulerConfig.class);
  
  @Test
  void testShouldntConfigSchedulerIsApplied() {
    contextRunner
        .withPropertyValues("compasso.votemanager.scheduler.enable=false")
        .run(context -> assertAll(
            () -> assertThat(context).doesNotHaveBean(SchedulerConfig.class)));
  }
}
