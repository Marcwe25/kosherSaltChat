package com.mw.security.app.modelView;

import com.mw.security.app.model.MemberRoom;
import com.mw.security.app.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberRoomCompletion {
    Room room;
    MemberRoom from;
    MemberRoom to;
}
