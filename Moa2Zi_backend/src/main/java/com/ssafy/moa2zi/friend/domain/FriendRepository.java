package com.ssafy.moa2zi.friend.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom{
    boolean existsByRequestIdAndAcceptId(Long requestId, Long acceptId);
}
