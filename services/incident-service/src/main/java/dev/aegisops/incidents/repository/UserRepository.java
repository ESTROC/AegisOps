package dev.aegisops.incidents.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.aegisops.incidents.domain.AppUser;
public interface UserRepository extends JpaRepository<AppUser,Long>{Optional<AppUser> findByUsername(String username);}
