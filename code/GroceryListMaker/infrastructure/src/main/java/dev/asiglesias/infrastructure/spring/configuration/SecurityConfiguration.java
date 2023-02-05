package dev.asiglesias.infrastructure.spring.configuration;

import dev.asiglesias.infrastructure.auth.services.JwtTokenService;
import dev.asiglesias.infrastructure.spring.filters.JwtTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    /*@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }*/

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtTokenService tokenService) throws Exception {
        httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().disable()
                .csrf().disable();
        httpSecurity.authorizeRequests()
                .antMatchers("/signup", "/signin").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtTokenAuthenticationFilter(tokenService), BasicAuthenticationFilter.class)
                .httpBasic();

        return httpSecurity.build();
    }

}
