package dev.aegisops.incidents.security;
import dev.aegisops.incidents.domain.AppUser;
import dev.aegisops.incidents.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class BootstrapAdmin {
 @Bean CommandLineRunner bootstrap(UserRepository repo,PasswordEncoder encoder,
 @Value("${app.bootstrap.username:}") String user,@Value("${app.bootstrap.password:}") String pass){
  return args->{
   if(user.isBlank()||pass.length()<12)throw new IllegalStateException("Set APP_BOOTSTRAP_USERNAME and a 12+ character APP_BOOTSTRAP_PASSWORD");
   if(repo.findByUsername(user).isEmpty())repo.save(new AppUser(user,encoder.encode(pass),"ADMIN"));
  };
 }
}
