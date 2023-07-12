package com.mw.security.app.services;

import com.mw.security.app.model.Member;
import com.mw.security.app.repositories.MemberRepository;
import com.mw.security.oauth2.Oauth2CustomUser;
import com.mw.security.oauth2.Oauth2CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private MemberRepository memberRepository;
    private Oauth2CustomUserService oauth2Service;

    @Autowired
    public MemberService(MemberRepository memberRepository, Oauth2CustomUserService oauth2CustomUserService) {

        this.memberRepository = memberRepository;
        this.oauth2Service = oauth2CustomUserService;
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member getMemberByUsername(String username) {
        Member member2 = memberRepository.findByUsername("nitza@rerere.com");
        Member member = memberRepository.findByUsername(username);
        return member;
    }

    public Member findRegisteredMember() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String email = authentication.getName();
        if(authentication instanceof JwtAuthenticationToken){
            JwtAuthenticationToken ja = (JwtAuthenticationToken)authentication;
            Optional<Oauth2CustomUser> bySub = oauth2Service.findBySub(email);
            if(bySub.isEmpty()) throw new Exception("coudn't find oauth2 user while he is registered with sub " + email);
            email = bySub.get().getEmail();
        }

        System.out.println("--- findRegisteredMember ---" + email);
        Member member = getMemberByUsername(email);
        return member;
    }

    public Member getOrSaveMemberByUsername(String username) {
        Member member = getMemberByUsername(username);
        if (member == null) {
            member = Member.builder().username(username).build();
            memberRepository.save(member);
        }
        return member;
    }

    public Member updateMember(Member memberUpdates) throws Exception{
        Member registeredMember = findRegisteredMember();
        if(memberUpdates.displayName!=null
                && memberUpdates.displayName.trim().length()>0){
            registeredMember.setDisplayName(memberUpdates.displayName);
        }
        return memberRepository.save(registeredMember);
    }

    public void deleteMember(Member chatMember) {
        memberRepository.delete(chatMember);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member createMember(Member member){
        return memberRepository.save(member);
    }

}