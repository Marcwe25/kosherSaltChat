package com.mw.security.user;

import com.mw.security.oauth2.Oauth2CustomUser;
import com.mw.security.token.Token;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Integer id;
  private String firstname;
  private String lastname;
  @Column(unique=true)
  private String email;
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @ToString.Exclude
  @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
  private List<Token> tokens;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Builder(builderMethodName = "fromOauthUser")
  public static User fromOauthUser(Oauth2CustomUser oauth2CustomUser){
    return User.builder()
            .email(oauth2CustomUser.getEmail())
            .firstname(oauth2CustomUser.getGiven_name())
            .lastname(oauth2CustomUser.getFamily_name())
            .role(Role.USER)
            .build();
  }
  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (this == o) return true;

    if (o instanceof Token that) {
      return this.id != null && Objects.equals(this.id, that.id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
