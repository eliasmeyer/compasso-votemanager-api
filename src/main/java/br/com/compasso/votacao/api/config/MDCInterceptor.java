package br.com.compasso.votacao.api.config;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCInterceptor implements HandlerInterceptor {
  
  private static final String KEY_MDC = "requestID";
  
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    MDC.put(KEY_MDC, getRequestId());
    return true;
  }
  
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    MDC.remove(KEY_MDC);
  }
  
  private String getRequestId() {
    return UUID.randomUUID().toString();
  }
}
