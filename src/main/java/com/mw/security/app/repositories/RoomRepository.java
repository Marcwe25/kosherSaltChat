package com.mw.security.app.repositories;

import com.mw.security.app.model.Member;
import com.mw.security.app.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {


    List<Room> findByIdAndEnabledTrue(long id);


}
