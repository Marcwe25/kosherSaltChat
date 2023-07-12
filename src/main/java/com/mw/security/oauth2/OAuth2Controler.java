package com.mw.security.oauth2;

import com.mw.security.app.model.Member;
import com.mw.security.app.services.MemberService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/Oauth2/member")
public class OAuth2Controler {

    MemberService memberService;
    Oauth2CustomUserService oauth2Service;

    @Autowired
    public OAuth2Controler(MemberService memberService, Oauth2CustomUserService oauth2CustomUserService) {
        this.memberService = memberService;
        this.oauth2Service = oauth2CustomUserService;
    }

    @GetMapping("/google")
    public Member googleAuthentication(JwtAuthenticationToken authentication) {
        Jwt credentials = (Jwt) authentication.getCredentials();
        Map<String, Object> claims = credentials.getClaims();
        Oauth2CustomUser oauth2User = Oauth2CustomUser.getFromClaims(claims);
        Optional<Oauth2CustomUser> bySub = oauth2Service.findBySub(oauth2User.getSub());
        if(bySub.isEmpty()) oauth2Service.saveOauth2User(oauth2User);
        String email = oauth2User.getEmail();
        Member member = memberService.getMemberByUsername(email);
        if(member == null) {
            member = Member.fromOauth2CustomUser(oauth2User);
            memberService.createMember(member);
        }
        return member;
    }




    @Data
    @Accessors(chain = true)
    private static class Info {
        private String application;
        private Map<String, Object> principal;
    }
}
