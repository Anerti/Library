package hei.school.library.config;

import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.UnauthorizedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter)
      throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/ping").permitAll()

            .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exceptions ->
            exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    throw new UnauthorizedException("Authentication required.");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    throw new ForbiddenException("Insufficient privileges.");
                })
        );

    return http.build();
  }
}
