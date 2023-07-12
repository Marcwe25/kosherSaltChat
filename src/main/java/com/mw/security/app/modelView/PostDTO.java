package com.mw.security.app.modelView;

import com.mw.security.app.model.Member;
import com.mw.security.app.model.Post;
import com.mw.security.app.model.Room;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@Builder
public class PostDTO {

    private Long id;
    public long from;
    public long room;
    public LocalDateTime dateTime;
    public String content;
    public boolean enabled = true;

    @Builder(builderMethodName = "PostBuilder")
    public static PostDTO fromPost(Post post){
        if(post==null) return null;
        return PostDTO
                .builder()
                .id(post.getId())
                .from(post.getFrom().getId())
                .room(post.getRoom().getId())
                .dateTime(post.getDateTime())
                .content(post.getContent())
                .enabled(post.isEnabled())
                .build();
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "from=" + from +
                ", room=" + room +
                ", dateTime=" + dateTime +
                ", content='" + content + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
