package jp.co.japantaxi.config;

import io.sentry.Sentry;
import io.sentry.spring.SentryExceptionResolver;
import io.sentry.spring.SentryServletContextInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SentryConfig {

  @Value("${sentry.dsn}")
  private String sentryDSN;

  @Bean
  public HandlerExceptionResolver sentryExceptionResolver() {
    if (sentryDSN != null && !sentryDSN.isEmpty()) {
      Sentry.init(sentryDSN);
    }
    return new SentryExceptionResolver();
  }

  @Bean
  public ServletContextInitializer sentryServletContextInitializer() {
    return new SentryServletContextInitializer();
  }
}
