package com.mw.security.token;

import com.mw.security.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

  @Id
  @GeneratedValue
  public Integer id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;

  public boolean expired;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  public User user;

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
