package com.mw.security.app.modelView;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Builder
@Data
public class RoomDTOList {

    List<RoomDTO> rooms = new ArrayList<>();
    Map<Long,MemberDTO> members = new HashMap();

}
