package com.mw.security.app.services;

import com.mw.security.app.exception.DuplicateMemberRoomException;
import com.mw.security.app.model.*;
import com.mw.security.app.modelView.*;
import com.mw.security.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MemberRoomService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final MemberService memberService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    String message = " would like to add you to his contact, click here to accept";

    @Autowired
    public MemberRoomService(
            RoomRepository roomRepository,
            MemberRepository memberRepository,
            PostRepository postRepository,
            MemberRoomRepository memberRoomRepository,
            MemberService memberService,
            SimpMessagingTemplate messagingTemplate,
            NotificationService notificationService,
            NotificationRepository notificationRepository
    ) {
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.memberRoomRepository = memberRoomRepository;
        this.memberService = memberService;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    public void process(MemberRoomRequest memberRoomRequest) throws Exception {
        Member registeredMember = memberService.findRegisteredMember();
        if (registeredMember.getUsername()==memberRoomRequest.getTo()) throw new DuplicateMemberRoomException(registeredMember.username);
        Member withMember = memberService.getMemberByUsername(memberRoomRequest.getTo());
        Room room = new Room();

        MemberRoom from = MemberRoom.builder()
                .member(registeredMember)
                .room(room)
                .lastSeen(LocalDateTime.now())
                .enable(true)
                .deleted(false)
                .build();
        registeredMember.addMemberRoom(from);
        room.addMemberRoom(from);

        MemberRoom to = MemberRoom.builder()
                .member(withMember)
                .room(room)
                .lastSeen(LocalDateTime.now())
                .enable(false)
                .deleted(false)
                .build();
        withMember.addMemberRoom(to);
        room.addMemberRoom(to);


        roomRepository.save(room);
        memberRepository.saveAll(Arrays.asList(registeredMember, withMember));
        memberRoomRepository.saveAll(Arrays.asList(from,to));
        notificationService.newNotificationAndSend(memberRoomRequest,to);
    }

    public MemberRoom getMemberRoom (Member member, Room room) {
        MemberRoom memberRoom = MemberRoom
                .builder()
                .member(member)
                .room(room)
                .lastSeen(LocalDateTime.now())
                .build();
        room.addMemberRoom(memberRoom);
        member.addMemberRoom(memberRoom);
        return memberRoom;
    }

    public MemberRoom createMemberRoom(Member member, Room room) {
        MemberRoom memberRoom = MemberRoom.builder().member(member).room(room).build();
        MemberRoom savedMemberRoom = memberRoomRepository.save(memberRoom);
        room.addMemberRoom(savedMemberRoom);
        roomRepository.save(room);
        member.addMemberRoom(savedMemberRoom);
        memberRepository.save(member);

        return savedMemberRoom;
    }

    public MemberRoom getMemberRoomForRegisteredUser(long roomId) throws Exception {
        Member member = memberService.findRegisteredMember();
        Room room = roomRepository.getReferenceById(roomId);
        MemberRoom memberRoom = memberRoomRepository.findFirstByRoomAndMember(room, member);
        return memberRoom;
    }

    public MemberRoom getByRoomAndByMember(long roomId, long memberId) {
        MemberRoom memberRoom = memberRoomRepository.findFirstByRoomIdAndMemberId(roomId, memberId);
        return memberRoom;
    }

    public boolean setLastSeen(long roomId) throws Exception {
        MemberRoom memberRoom = getMemberRoomForRegisteredUser(roomId);
        memberRoom.setLastSeen(LocalDateTime.now());
        memberRoomRepository.save(memberRoom);
        return true;
    }

    public List<MemberRoom> getMemberRoomsForRoomId(long roomid){
        List<MemberRoom> memberRooms = memberRoomRepository.findByRoomId(roomid);
        return memberRooms;
    }


    public RoomDTOList getRoomsForUser(long id) {
        Map<Long, LocalDateTime> lastSeen = new HashMap<>();
        Map<Long, MemberDTO> memberDTOs = new HashMap<>();
        List<RoomDTO> roomDTOs =
                memberRoomRepository
                        .findByMemberIdAndDeletedFalse(id)
                        .parallelStream()
                        .peek(mr -> lastSeen.putIfAbsent(mr.getRoom().getId(), mr.getLastSeen()))
                        .map(mr -> mr.getRoom().getId())
                        .collect(Collectors.toMap(Function.identity(),
                                        rid -> memberRoomRepository
                                                .findByRoomId(rid)
                                                .parallelStream()
                                                .map(rm -> rm.getMember())
                                                .peek(m -> memberDTOs.putIfAbsent(m.getId(), MemberDTO.fromMember(m)))
                                                .map(m -> m.getId())
                                )
                        )
                        .entrySet()
                        .parallelStream()
                        .map(
                                (entry) -> {
                                    return RoomDTO
                                            .builder()
                                            .id(entry.getKey())
                                            .members(entry
                                                    .getValue()
                                                    .collect(Collectors.toList()))
                                            .unread(
                                                    postRepository.countByRoomIdAndDateTimeIsAfter(entry.getKey(), lastSeen.get(entry.getKey()))
                                            )
                                            .lastPost(
                                                    PostDTO.fromPost(
                                                            postRepository
                                                                    .findFirstByRoomOrderByDateTimeDesc(
                                                                            roomRepository.getReferenceById(entry.getKey())
                                                                    )
                                                    )
                                            ).memberRoomEnable(
                                                    getByRoomAndByMember(entry.getKey(),id).isEnable()
                                            )
                                            .build();
                                }
                        ).collect(Collectors.toList()
                        );

        RoomDTOList dtoList = RoomDTOList
                .builder()
                .members(memberDTOs)
                .rooms(roomDTOs)
                .build();

        return dtoList;
    }

//    public void process(MemberRoomRequest memberRoomRequest){
//        MemberRoomCompletion roomRequestCompletion = createEntitiesForMemberRoomRequest(null, memberRoomRequest.getEmail());
//
//        PostDTO postDTO = PostDTO
//                .builder()
//                .room(roomRequestCompletion.getRoom().getId())
//                .from(memberRoomRequest.getFrom())
//                .dateTime(memberRoomRequest.getIssued())
//                .content(memberRoomRequest.getMessage())
//                .build();
//
//        String to = memberRoomRequest.getEmail();
//        messagingTemplate.convertAndSendToUser(
//                to,
//                "/user/queue/to",
//                postDTO);
//        Notification.builder()
//                .type(MemberRoom.class)
//                .typeId(memberRoom.getId())
//                .to(memberRoom.getMember())
//                .message("enablenewroom")
//                .dateTime(LocalDateTime.now())
//                .build()
//
//        roomRequestCompletion.getMemberRooms()
//                .parallelStream()
//                .map(memberRoom ->
//                    Notification.builder()
//                            .type(MemberRoom.class)
//                            .typeId(memberRoom.getId())
//                            .to(memberRoom.getMember())
//                            .message("enablenewroom")
//                            .dateTime(LocalDateTime.now())
//                            .build()
//                )
//                .peek(notification ->
//                        messagingTemplate
//                                .convertAndSendToUser(
//                        to,
//                        "/user/queue/to",
//                        postDTO);)
//
//    }

    public void setLinked(long notificqationId, boolean isLinked) throws Exception{
        Member registeredMember = memberService.findRegisteredMember();
        Optional<Notification> opNotification = notificationRepository.findById(notificqationId);
        Notification notification = opNotification.get();
        NotificationType type = notification.getType();

        if(type==NotificationType.NewContact) {
            Optional<MemberRoom> opMemberRoom = memberRoomRepository.findById(notification.getTypeId());
            MemberRoom memberRoom = opMemberRoom.get();
            memberRoom.setEnable(isLinked);
            memberRoom.setDeleted(!isLinked);
            memberRoomRepository.save(memberRoom);
            notification.setEnable(false);
            notificationRepository.save(notification);

        }

    }

}
