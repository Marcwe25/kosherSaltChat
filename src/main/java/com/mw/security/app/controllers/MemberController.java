package com.mw.security.app.controllers;


import com.mw.security.app.model.Member;
import com.mw.security.app.model.Notification;
import com.mw.security.app.modelView.MemberDTO;
import com.mw.security.app.modelView.NotificationsDTOList;
import com.mw.security.app.modelView.RoomDTOList;
import com.mw.security.app.services.MemberRoomService;
import com.mw.security.app.services.MemberService;
import com.mw.security.app.services.NotificationService;
import com.mw.security.app.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true", methods = RequestMethod.GET,allowedHeaders = "Authorization:")
public class MemberController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final MemberService memberService;
    private final MemberRoomService memberRoomService;
    private final NotificationService notificationService;

    @GetMapping("/rooms")
    public RoomDTOList getRoomsForUser() throws Exception {
        Member member = memberService.findRegisteredMember();
        System.out.println("xxx " + member.getUsername());
        RoomDTOList dtoList = memberRoomService.getRoomsForUser(member.getId());
        return dtoList;
    }

    @GetMapping
    public Member getRegisteredMember() throws Exception{
        Member member = memberService.findRegisteredMember();
        return member;
    }

    @PutMapping
    public MemberDTO updateMember(@RequestBody Member member) throws Exception {
        Member updatedMember = memberService.updateMember(member);
        return MemberDTO.fromMember(updatedMember);
    }

    @PutMapping("/unlink/{roomId}")
    public void unlinkFromRoom(@PathVariable(name = "roomId") Long roomId) throws Exception {
        System.out.println("ssseting controler deleted " + roomId);
        memberRoomService.setLinked(roomId,false);
    }

    @PutMapping("/link/{roomId}")
    public void linkFromRoom(@PathVariable(name = "roomId") Long notificqationId) throws Exception {
        memberRoomService.setLinked(notificqationId,true);
    }

    @GetMapping("/notifications")
    public NotificationsDTOList getNotifications() throws Exception {
        NotificationsDTOList notificationsDTOList = notificationService.getNotifications();
        System.out.println("zzz3 - ");
        System.out.println(notificationsDTOList);
        return notificationsDTOList;
    }

}
