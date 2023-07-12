package com.mw.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Oauth2CustomUserService {
    Oauth2CustomUserRepository oauth2Repository;

    @Autowired
    public Oauth2CustomUserService(Oauth2CustomUserRepository oauth2Repository) {
        this.oauth2Repository = oauth2Repository;
    }

    public Optional<Oauth2CustomUser> findBySub(String sub) {
        return oauth2Repository.findBySub(sub);
    }


    public void saveOauth2User(Oauth2CustomUser oauth2CustomUser){
        oauth2Repository.save(oauth2CustomUser);
    }
}
