package com.mw.security.app.repositories;


import com.mw.security.app.model.Member;
import com.mw.security.app.model.MemberRoom;
import com.mw.security.app.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRoomRepository  extends JpaRepository<MemberRoom, Long> {

    List<MemberRoom> findByMemberId(long member_id);
    List<MemberRoom> findByMemberIdAndDeletedFalse(long member_id);

    List<MemberRoom> findByRoomId(long room_id);

    MemberRoom findFirstByRoomAndMember(Room room, Member member );
    MemberRoom findFirstByRoomIdAndMemberId(long room_id, long member_id );

}

