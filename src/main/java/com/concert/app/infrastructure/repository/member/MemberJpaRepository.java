package com.concert.app.infrastructure.repository.member;

import com.concert.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}