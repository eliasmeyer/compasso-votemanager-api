package br.com.compasso.votacao.api.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareDefaultImpl implements AuditorAware<String> {
  
  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of("spring-application");
  }
}
