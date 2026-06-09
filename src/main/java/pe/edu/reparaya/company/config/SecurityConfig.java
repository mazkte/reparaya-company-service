package pe.edu.reparaya.company.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

 private static final String[] PUBLIC_ENDPOINTS = {
   "/actuator/**",
   "/v3/api-docs/**",
   "/swagger-ui/**",
   "/swagger-ui.html"
 };

 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http
    .csrf(AbstractHttpConfigurer::disable)
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .sessionManagement(session ->
      session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
      .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
      .anyRequest().authenticated()
    )
    .oauth2ResourceServer(oauth2 -> oauth2
      .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
    );

  return http.build();
 }

 @Bean
 public JwtAuthenticationConverter jwtAuthenticationConverter() {
  JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
  converter.setJwtGrantedAuthoritiesConverter(jwt -> {
   Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
   if (realmAccess == null) return List.of();
   Object roles = realmAccess.get("roles");
   if (!(roles instanceof Collection<?> roleList)) return List.of();
   return roleList.stream()
     .filter(r -> r instanceof String)
     .map(r -> new SimpleGrantedAuthority((String) r))
     .collect(Collectors.toList());
  });
  return converter;
 }

 @Bean
 public CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration config = new CorsConfiguration();
  config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost"));
  config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
  config.setAllowedHeaders(List.of("*"));
  config.setAllowCredentials(true);
  config.setMaxAge(3600L);
  UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
  source.registerCorsConfiguration("/**", config);
  return source;
 }
}