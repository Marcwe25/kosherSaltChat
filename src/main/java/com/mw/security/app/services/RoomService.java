package com.mw.security.app.services;


import com.mw.security.app.model.Member;
import com.mw.security.app.model.Post;
import com.mw.security.app.model.Room;
import com.mw.security.app.modelView.PostDTO;
import com.mw.security.app.repositories.MemberRepository;
import com.mw.security.app.repositories.MemberRoomRepository;
import com.mw.security.app.repositories.PostRepository;
import com.mw.security.app.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberRoomRepository memberRoomRepository;

    @Autowired
    public RoomService(
            RoomRepository roomRepository,
            MemberRepository memberRepository,
            PostRepository postRepository,
            MemberRoomRepository memberRoomRepository
    ) {
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.memberRoomRepository = memberRoomRepository;
    }



    public List<Room> createRoom(List<Room> Rooms) {
        List<Room> unsaved = new ArrayList<>();
        for(Room Room : Rooms){
            roomRepository.save(Room);
        }
        return unsaved;
    }
    public Room saveRoom(Room Room) {

        return roomRepository.save(Room);
    }



    public Room findRoomById(Long id) {
        return roomRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(" room not found"));
    }

    public Post savePost(Post Post) {
        return postRepository.save(Post);
    }

    public boolean disableRoom(long roomId){
        System.out.println("service trying to disable room");
        Room Room;
        Room = roomRepository.findById(roomId).orElseThrow();
        Room.setEnabled(false);
        roomRepository.save(Room);
        return true;
    }
    @Transactional
    public boolean addPostToRoom(Long roomId, Post post){
        Member member = memberRepository.findByUsername(post.getFrom().getUsername());
        Room room = roomRepository.findById(roomId).orElseThrow();
        post.setRoom(room);
        post.setFrom(member);
        postRepository.save(post);
        return true;
    }

    public List<PostDTO> getLast10PostsForRoom(long roomId){
        Sort sort = Sort.by("dateTime").descending();
        return postRepository
                .findPostByRoomId(roomId,sort)
                .stream()
                .map(p->PostDTO.fromPost(p))
                .collect(Collectors.toList());
    }

}

//    public List<Room> findAllRooms() {
//        return RoomRepository.findAll();
//    }
//    public List<Room> findRoomsByMemberUsername(String username) {
//        Member Member = MemberRepository.findByUsername(username);
//        List<Room> Rooms = RoomRepository.findByEnabledTrueAndMembersContaining(Member);
//        System.out.println("service");
//        for(Room c : Rooms){
//            System.out.println(c.getId());
//            Set<Member> Members = c.getMembers();
//            System.out.println(Members);
//        }        return Rooms;
//    }

//    public List<Room> findOwnRooms(){
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        String email = securityContext.getAuthentication().getName();
//        List<Room> rooms = findRoomsByMemberUsername(email);
//        for(Room room : rooms){
//            Set<Member> members = room.getMembers();
//            for (Member member : members){
//                members.add(MemberRepository.findByUsername(member.getUsername()));
//            }
//            room.setMembers(members);
//        }
//        return rooms;
//    }

//    public boolean deleteRoom(long roomId){
//        System.out.println("service trying to delete room");
//        Room Room = RoomRepository.findById(roomId).orElseThrow();
//        Room.getMembers().clear();
//        RoomRepository.deleteById(roomId);
//        RoomRepository.save(Room);
//        return true;
//    }

//    public Room createRoom(String name, Set<String> usernames) {
//        List<MemberRoom> memberRooms = new ArrayList<>();
//
//        Room room = new Room();
//        room.setName(name);
//        for (String username : usernames) {
//            Member member = MemberRepository.findByUsername(username);
//            MemberRoom memberRoom = MemberRoom.builder().member(member).room(room).build();
//
//            memberRooms.add(memberRoom);
//        }
//        memberRoomRepository.saveAll(memberRooms);
//        return RoomRepository.save(room);
//    }