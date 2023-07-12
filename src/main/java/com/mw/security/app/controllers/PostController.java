package com.mw.security.app.controllers;


import com.mw.security.app.exception.MemberRoomDisabled;
import com.mw.security.app.model.Member;
import com.mw.security.app.model.MemberRoom;
import com.mw.security.app.model.Post;
import com.mw.security.app.model.Room;
import com.mw.security.app.modelView.PostDTO;
import com.mw.security.app.services.MemberRoomService;
import com.mw.security.app.services.MemberService;
import com.mw.security.app.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final MemberService memberService;
    private final MemberRoomService memberRoomService;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload Post post) throws Exception {
        System.out.println("got message : " + post);
        List<MemberRoom> memberRooms = memberRoomService.getMemberRoomsForRoomId(roomId);
        roomService.addPostToRoom(roomId, post);
        messagingTemplate.convertAndSend("/topic/" + roomId, post);
    }

    @MessageMapping("/request/{roomId}")
    public void requestResponse(@DestinationVariable Long roomId, @Payload Boolean response) throws Exception{
        System.out.println("got requestResponse : " + response);
        if(response){
            Member member = memberService.findRegisteredMember();
            String postMessage = member.getDisplayName() + " accepted, you're now connected";
            PostDTO postDTO = PostDTO
                    .builder()
                    .room(roomId)
                    .content(postMessage)
                    .dateTime(LocalDateTime.now())
                    .from(member.getId())
                    .build();
            MemberRoom memberRoom = memberRoomService.getByRoomAndByMember(roomId, member.getId());
            memberRoom.setEnable(true);
            messagingTemplate.convertAndSend("/topic/" + roomId, postDTO);
        }
        else {
            Member member = memberService.findRegisteredMember();
            String postMessage = member.getDisplayName() + " declined, you're not connected";
            PostDTO postDTO = PostDTO
                    .builder()
                    .room(roomId)
                    .content(postMessage)
                    .dateTime(LocalDateTime.now())
                    .from(member.getId())
                    .build();
            MemberRoom memberRoom = memberRoomService.getByRoomAndByMember(roomId, member.getId());
            memberRoom.setEnable(false);
            messagingTemplate.convertAndSend("/topic/" + roomId, postDTO);
        }
    }
    @MessageMapping("/{roomId}incoming")
    public void newMessageNothification(@DestinationVariable Long roomId, @Payload Post post) {

        System.out.println("got message : " + post);
        roomService.addPostToRoom(roomId, post);
        messagingTemplate.convertAndSend("/topic/" + roomId, post);
    }

}

//    @PostMapping("/api/Rooms/{roomId}/posts")
//    public List<Post> getPostsByRoomId(@RequestBody Long roomId, @RequestBody LocalDateTime dateTime) {
//        return roomService.findPostsByRoomIdAndDateTime(roomId,dateTime);
//    }