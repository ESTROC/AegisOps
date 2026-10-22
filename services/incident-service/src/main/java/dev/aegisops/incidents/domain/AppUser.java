package dev.aegisops.incidents.domain;
import jakarta.persistence.*;
@Entity
@Table(name="app_users")
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String username;
 @Column(name="password_hash",nullable=false) private String passwordHash;
 @Column(nullable=false) private String role;
 @Column(nullable=false) private boolean enabled=true;
 protected AppUser(){}
 public AppUser(String username,String passwordHash,String role){this.username=username;this.passwordHash=passwordHash;this.role=role;}
 public String getUsername(){return username;}
 public String getPasswordHash(){return passwordHash;}
 public String getRole(){return role;}
 public boolean isEnabled(){return enabled;}
}
