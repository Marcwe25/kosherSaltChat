package com.mw.security.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mw.security.oauth2.Oauth2CustomUser;
import com.mw.security.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "username is mandatory")
    public String username;
    public String displayName;
    public String pictureUrl;

    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<MemberRoom> memberRooms = new HashSet<>();

    @OneToMany(mappedBy = "from",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore public Set<Post> chatPosts = new HashSet<>();

    boolean enabled = true;

    @Builder(builderMethodName = "modelBuilder")
    public static Member newMember(User user){
        return Member.builder().username(user.getEmail()).build();
    }

    @Builder(builderMethodName = "fromOauth2CustomUser")
    public static Member fromOauth2CustomUser(Oauth2CustomUser oauth2CustomUser){
        return Member
                .builder()
                .username(oauth2CustomUser.getEmail())
                .displayName(oauth2CustomUser.getName())
                .pictureUrl(oauth2CustomUser.getPicture())
                .build();
    }
    public void addMemberRoom(MemberRoom memberRoom){
        memberRooms.add(memberRoom);
    }

    public void removeMemberRoom(MemberRoom memberRoom){
        memberRooms.remove(memberRoom);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<MemberRoom> getMemberRooms() {
        return memberRooms;
    }

    public void setMemberRooms(Set<MemberRoom> memberRooms) {
        this.memberRooms = memberRooms;
    }

    public Set<Post> getChatPosts() {
        return chatPosts;
    }

    public void setChatPosts(Set<Post> chatPosts) {
        this.chatPosts = chatPosts;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        return getUsername().equals(member.getUsername());
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
