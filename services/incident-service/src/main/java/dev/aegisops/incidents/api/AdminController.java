package dev.aegisops.incidents.api;
import dev.aegisops.incidents.domain.AppUser;
import dev.aegisops.incidents.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/admin") @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
 private final UserRepository users;private final PasswordEncoder passwords;
 public AdminController(UserRepository users,PasswordEncoder passwords){this.users=users;this.passwords=passwords;}
 public record CreateUser(@NotBlank @Size(max=100)String username,@NotBlank @Size(min=12,max=100)String password,@NotNull @Pattern(regexp="ADMIN|ENGINEER|VIEWER")String role){}
 @PostMapping("/users")@ResponseStatus(HttpStatus.CREATED)
 public Map<String,String> provision(@Valid @RequestBody CreateUser req){
  if(users.findByUsername(req.username()).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Username exists");
  AppUser user=users.save(new AppUser(req.username(),passwords.encode(req.password()),req.role()));
  return Map.of("username",user.getUsername(),"role",user.getRole());
 }
}
