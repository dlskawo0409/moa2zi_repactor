package com.ssafy.moa2zi.lounge_participant.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoungeParticipantRepository extends JpaRepository<LoungeParticipant, Long>, LoungeParticipantRepositoryCustom {
    boolean existsByMemberIdAndLoungeId(Long memberId, Long loungeId);
}
