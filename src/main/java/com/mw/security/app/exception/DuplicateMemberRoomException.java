package com.mw.security.app.exception;

import com.mw.security.app.model.MemberRoom;

public class DuplicateMemberRoomException extends AppException {

    private String username;
    private Long roomId;


    public DuplicateMemberRoomException(MemberRoom memberRoom) {
        super();
        this.username = memberRoom.getMember().getUsername();
        roomId = memberRoom.getRoom().getId();
    }

    public DuplicateMemberRoomException(String username, Long roomId) {
        super();
        this.username = username;
        roomId = roomId;
    }

    public DuplicateMemberRoomException(String username) {
        super();
        this.username = username;
    }

    public DuplicateMemberRoomException() {
        super();
    }

    private String getExceptionMessage (String username , Long id) {
        return "member " + username + " already associated to room " + Long.toString(id);
    }
}
