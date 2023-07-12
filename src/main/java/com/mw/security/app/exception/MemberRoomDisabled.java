package com.mw.security.app.exception;

import com.mw.security.app.model.MemberRoom;

public class MemberRoomDisabled extends AppException {

    private String username;
    private Long roomId;


    public MemberRoomDisabled(MemberRoom memberRoom) {
        super();
        this.username = memberRoom.getMember().getUsername();
        roomId = memberRoom.getRoom().getId();
    }

    public MemberRoomDisabled(String username, Long roomId) {
        super();
        this.username = username;
        roomId = roomId;
    }

    public MemberRoomDisabled(String username) {
        super();
        this.username = username;
    }

    public MemberRoomDisabled() {
        super();
    }

    private String getExceptionMessage (String username , Long id) {
        return "member " + username + " did not enable room " + Long.toString(id);
    }
}
