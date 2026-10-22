package dev.aegisops.incidents.api;
import dev.aegisops.incidents.domain.AppUser;
import dev.aegisops.incidents.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users;private final PasswordEncoder passwords;private final JwtEncoder jwt;
 private final String issuer;
 public AuthController(UserRepository users,PasswordEncoder passwords,JwtEncoder jwt,@Value("${app.jwt.issuer}")String issuer){this.users=users;this.passwords=passwords;this.jwt=jwt;this.issuer=issuer;}
 public record LoginRequest(@NotBlank String username,@NotBlank String password){}
 @PostMapping("/login")
 public Map<String,Object> login(@Valid @RequestBody LoginRequest request){
  AppUser user=users.findByUsername(request.username()).filter(AppUser::isEnabled).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));
  if(!passwords.matches(request.password(),user.getPasswordHash()))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");
  Instant now=Instant.now();
  JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).issuedAt(now).expiresAt(now.plus(30,ChronoUnit.MINUTES)).subject(user.getUsername()).claim("roles",List.of(user.getRole())).build();
  String token=jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
  return Map.of("accessToken",token,"tokenType","Bearer","expiresIn",1800,"role",user.getRole(),"username",user.getUsername());
 }
}
