package com.mw.security.app.repositories;

import com.mw.security.app.model.Member;
import com.mw.security.app.model.MemberRoom;
import com.mw.security.app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {

    List<Notification> findAllByTo(Member to);
    List<Notification> findAllByToAndEnableIsTrue(Member to);

}
