package com.ssafy.moa2zi.member.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>,MemberRepositoryCustom {

    Optional<Member> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
    Boolean existsByPhoneNumber(String phoneNumber);
    Optional<Member> findByPhoneNumber(String phoneNumber);
    void delete(Member member);

}
