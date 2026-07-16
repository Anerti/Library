package hei.school.library.config;

import hei.school.library.exception.ErrorBody;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
      HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers("/ping")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/libraries")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/libraries/{libraryId}")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/libraries")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/libraries/{libraryId}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/books", "/books/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/books", "/books/{bookId}/verify")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/books")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/books/{id}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/books/{id}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/books/{bookId}/stock")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/libraries/{libraryId}/copies",
                        "/libraries/{libraryId}/copies/{copyId}")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/libraries/{libraryId}/copies")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/libraries/{libraryId}/copies/{copyId}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/libraries/{libraryId}/copies/{copyId}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/authors", "/authors/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/authors")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/authors/{id}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/authors/{id}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/genres", "/genres/{genreId}")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/genres")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/genres/{genreId}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/genres/{genreId}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/users/{id}")
                    .hasAnyRole("ADMIN", "CUSTOMER")
                    .requestMatchers(HttpMethod.GET, "/users")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/users/{id}")
                    .hasAnyRole("ADMIN", "CUSTOMER")
                    .requestMatchers(HttpMethod.DELETE, "/users/{id}")
                    .hasAnyRole("ADMIN", "CUSTOMER")
                    .requestMatchers(
                        HttpMethod.GET,
                        "/libraries/{libraryId}/arrivals",
                        "/libraries/{libraryId}/arrivals/{arrivalId}")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/libraries/{libraryId}/arrivals")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/arrivals/{arrivalId}/items")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/arrivals/{arrivalId}/items")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/arrivals/{arrivalId}/items/{bookCopyId}")
                    .hasRole("ADMIN")
                    .requestMatchers(
                        HttpMethod.GET,
                        "/libraries/{libraryId}/sales",
                        "/libraries/{libraryId}/sales/{saleId}")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/libraries/{libraryId}/sales")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/libraries/{libraryId}/sales/{saleId}")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/sales/{saleId}/items")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/sales/{saleId}/items")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/sales/{saleId}/items/{bookCopyId}")
                    .hasRole("ADMIN")
                    .requestMatchers(
                        HttpMethod.GET, "/libraries/{libraryId}/analytics/stock/{bookId}")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/libraries/{libraryId}/analytics/low-stock/book/{bookId}",
                        "/libraries/{libraryId}/analytics/revenue/by-genre")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                            ErrorBody.send(
                                response, HttpStatus.UNAUTHORIZED, "Authentication required."))
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) ->
                            ErrorBody.send(
                                response, HttpStatus.FORBIDDEN, "Insufficient privileges.")));

    return http.build();
  }
}
