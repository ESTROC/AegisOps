package dev.aegisops.incidents.security;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableMethodSecurity
public class SecurityConfiguration {
 @Bean SecretKey secretKey(@Value("${app.jwt.secret}") String raw){
  if(raw==null||raw.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalStateException("APP_JWT_SECRET must be at least 32 UTF-8 bytes");
  return new SecretKeySpec(raw.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
 }
 @Bean JwtEncoder jwtEncoder(SecretKey key){return new NimbusJwtEncoder(new ImmutableSecret<>(key));}
 @Bean JwtDecoder jwtDecoder(SecretKey key,@Value("${app.jwt.issuer}") String issuer){
  NimbusJwtDecoder decoder=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));return decoder;
 }
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean JwtAuthenticationConverter jwtAuthenticationConverter(){
  JwtAuthenticationConverter c=new JwtAuthenticationConverter();
  c.setJwtGrantedAuthoritiesConverter(jwt->{List<String> roles=jwt.getClaimAsStringList("roles");return roles==null?List.of():roles.stream().map(r->(GrantedAuthority)new SimpleGrantedAuthority("ROLE_"+r)).toList();});
  return c;
 }
 @Bean SecurityFilterChain chain(HttpSecurity http,JwtAuthenticationConverter converter)throws Exception{
  return http.csrf(csrf->csrf.disable())
   .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(auth->auth.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
    .requestMatchers("/api/auth/login","/actuator/health","/actuator/health/**","/actuator/info","/actuator/prometheus","/swagger-ui/**","/v3/api-docs/**","/error").permitAll()
    .anyRequest().authenticated())
   .oauth2ResourceServer(oauth->oauth.jwt(jwt->jwt.jwtAuthenticationConverter(converter))).build();
 }
}
