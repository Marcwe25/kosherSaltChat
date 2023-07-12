package com.mw.security.app.controllers;

import com.mw.security.app.model.Member;
import com.mw.security.app.model.MemberRoom;
import com.mw.security.app.model.Room;
import com.mw.security.app.modelView.MemberRoomRequest;
import com.mw.security.app.modelView.PostDTO;
import com.mw.security.app.services.MemberRoomService;
import com.mw.security.app.services.MemberService;
import com.mw.security.app.services.RoomService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final MemberService memberService;
    private final MemberRoomService memberRoomService;
    @PutMapping("/{roomId}")
    public void updateroom(@RequestParam long roomId, @RequestBody Room room) {
        room.setId(roomId);
        roomService.saveRoom(room);
    }

    @PutMapping("/addUserToRoom/{roomId}")
    public void addUserToRoom(@PathVariable(name = "roomId") Long roomId, @RequestBody Member member) {
        System.out.println("mytestroomid " + roomId);
        System.out.println("mytestuisername " + member.username);

        try {
            Room room = roomService.findRoomById(roomId);
            Member memberToAdd = memberService.getMemberByUsername(member.username);

            memberRoomService.createMemberRoom(memberToAdd,room);
        } catch (Exception e) {
            System.err.println("myerror");
            System.err.println(e);
            throw e;
        }
    }

    @PutMapping("/addUsersToRoom/{roomId}")
    public void addUsersToRoom(@PathVariable(name = "roomId") Long roomId, @RequestBody Set<Long> ids) {
        System.out.println("addingxxxx"+ids);
        try {
            Room room = roomService.findRoomById(roomId);
            Set<Member> members = new HashSet<>();
            for (Long id : ids) {
                Member memberToAdd = memberService.getMemberById(id);
                members.add(memberToAdd);
            }
            for(Member memberToAdd : members){
                memberRoomService.createMemberRoom(memberToAdd,room);

            }

        } catch (Exception e) {
            System.err.println("myerror");
            System.err.println(e);
            throw e;
        }
        System.out.println("addedxxxx"+ids);

    }

    @PostMapping
    public void newroom(@RequestBody MemberRoomRequest memberRoomRequest) throws Exception {
        System.out.println("reyyyy" + memberRoomRequest);
        memberRoomService.process(memberRoomRequest);
    }

    @GetMapping("/{roomId}")
    public Room getroomById(@RequestParam Long roomId) {
        Room room = roomService.findRoomById(roomId);
        return room;
    }

    @GetMapping("/posts/{roomId}")
    public List<PostDTO> findTop10ByRoomIdOrderBydate_time(@PathVariable(name = "roomId") Long roomId) throws Exception {
        memberRoomService.setLastSeen(roomId);
        return roomService.getLast10PostsForRoom(roomId);
    }

    @GetMapping("/lastSeen/{roomId}")
    public ResponseEntity setLastSeen(@PathVariable(name = "roomId") Long roomId) throws Exception {
        memberRoomService.setLastSeen(roomId);
        System.out.println("setting last seen  for " + roomId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
