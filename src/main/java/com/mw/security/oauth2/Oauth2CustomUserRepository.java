package com.mw.security.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Oauth2CustomUserRepository extends JpaRepository<Oauth2CustomUser,Long> {

    Optional<Oauth2CustomUser> findBySub(String sub);
}
