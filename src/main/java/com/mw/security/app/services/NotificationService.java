package com.mw.security.app.services;

import com.mw.security.app.model.Member;
import com.mw.security.app.model.MemberRoom;
import com.mw.security.app.model.Notification;
import com.mw.security.app.model.NotificationType;
import com.mw.security.app.modelView.MemberRoomRequest;
import com.mw.security.app.modelView.NotificationsDTOList;
import com.mw.security.app.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               NotificationRepository notificationRepository,
                               MemberService memberService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.memberService = memberService;
    }

    public Notification newNotification(MemberRoomRequest memberRoomRequest, MemberRoom memberRoom) {
        Notification newContactNotification = Notification.builder()
                .type(NotificationType.NewContact)
                .typeId(memberRoom.getId())
                .from(memberService.getMemberById(memberRoomRequest.getFrom()))
                .to(memberRoom.getMember())
                .message(memberRoomRequest.getMessage())
                .dateTime(LocalDateTime.now())
                .enable(true)
                .build();
        notificationRepository.save(newContactNotification);
        return newContactNotification;
    }

    public void newNotificationAndSend(MemberRoomRequest memberRoomRequest, MemberRoom memberRoom) {
        Notification newContactNotification = newNotification(memberRoomRequest, memberRoom);
        messagingTemplate.convertAndSendToUser(
                memberRoom.getMember().getUsername(),
                "/user/queue/to",
                newContactNotification
        );
    }

    public NotificationsDTOList getNotifications() throws Exception {
        Member member = memberService.findRegisteredMember();
        List<Notification> notifications = notificationRepository.findAllByTo(member);
        System.out.println("zzz - "+ notifications);
        NotificationsDTOList notificationsDTOList = NotificationsDTOList.fromNotifications(notifications);
        System.out.println("zzz2 - " + notificationsDTOList);
        return notificationsDTOList;
    }

    public Optional<Notification> getById(long id){
        return notificationRepository.findById(id);
    }

}
