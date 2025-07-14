package com.ssafy.moa2zi.chat_room_read.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomReadRepository extends JpaRepository<ChatRoomRead, Long> {
    Optional<ChatRoomRead> findByLoungeIdAndMemberId(Long loungeId, Long memberId);
    List<ChatRoomRead> findByMemberId(Long memberId);
}
