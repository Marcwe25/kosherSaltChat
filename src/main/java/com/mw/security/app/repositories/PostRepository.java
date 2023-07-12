package com.mw.security.app.repositories;

import com.mw.security.app.model.Post;
import com.mw.security.app.model.Room;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
//    List<Post> findByRoomIdAndByDateTimeAfter(Long roomId, LocalDateTime dateTime);
//    Post save(Post chatPost);

    public int countByRoomIdAndDateTimeIsAfter(long roomId,LocalDateTime dateTime);

    public List<Post> findPostByRoomId(long roomId, Sort sort);
    public Post findFirstByRoomOrderByDateTimeAsc(Room room);
    public Post findFirstByRoomOrderByDateTimeDesc(Room room);

}
