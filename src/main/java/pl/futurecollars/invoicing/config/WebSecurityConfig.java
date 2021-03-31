package pl.futurecollars.invoicing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CorsFilter corsFilter;

  @Value("${invoicing-system.cors.disable:false}")
  private boolean disableCors;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic()
        .and()
        .addFilterBefore(corsFilter, ChannelProcessingFilter.class);

    if (disableCors) {
      http.csrf().disable();
    } else {
      http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    }

  }

}
