package com.mw.security.app.modelView;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class RoomDTO {

    private Long id;
    public String name;
    public List<Long> members;
    public int unread;
    public PostDTO lastPost;
    public boolean memberRoomEnable;

}
