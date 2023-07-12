package com.mw.security.app.repositories;

import com.mw.security.app.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);

}
