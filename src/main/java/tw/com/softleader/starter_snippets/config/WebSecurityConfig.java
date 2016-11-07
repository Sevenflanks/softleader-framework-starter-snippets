package tw.com.softleader.starter_snippets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import tw.com.softleader.security.authentication.MoreDetailsBinder;
import tw.com.softleader.security.authentication.MoreUserDetailsService;
import tw.com.softleader.security.config.MoreWebSecurityConfiguration;
import tw.com.softleader.starter_snippets.security.service.UserDetailsService;

import tw.com.softleader.security.supplier.CurrentUsernameSupplier;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends MoreWebSecurityConfiguration {

  @Bean
  @Override
  public MoreDetailsBinder moreDetailsBinder() {
    return (request, details) -> {
      String channelCode = request.getParameter("channelCode");
      details.getMore().put("channelCode", channelCode);
    };
  }

  @Bean
  @Override
  public MoreUserDetailsService moreUserDetailsService() {
    return new UserDetailsService();
  }

  @Bean
  public CurrentUsernameSupplier currentUsernameSupplier() {
    return new CurrentUsernameSupplier();
  }
}