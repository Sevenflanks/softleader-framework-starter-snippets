package tw.com.softleader.starter_snippets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import tw.com.softleader.security.supplier.CurrentUsernameSupplier;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"tw.com.softleader.starter_snippets.**.service"})
public class ServiceConfig {

  @Bean
  public CurrentUsernameSupplier currentUsernameSupplier() {
    return new CurrentUsernameSupplier();
  }

}