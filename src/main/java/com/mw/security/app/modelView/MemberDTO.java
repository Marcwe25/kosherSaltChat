package com.mw.security.app.modelView;

import com.mw.security.app.model.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDTO {

    private Long id;
    public String username;
    public String displayName;

    @Builder(builderMethodName = "memberBuilder")
    public static MemberDTO fromMember(Member member){
        return MemberDTO
                .builder()
                .id(member.getId())
                .username(member.getUsername())
                .displayName(member.getDisplayName())
                .build();
    }

}
