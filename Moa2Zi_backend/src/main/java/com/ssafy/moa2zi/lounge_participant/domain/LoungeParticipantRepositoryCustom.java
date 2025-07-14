package com.ssafy.moa2zi.lounge_participant.domain;

import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;

import java.util.List;

public interface LoungeParticipantRepositoryCustom {
    boolean existsByLoungeIdAndMemberId(Long LoungeId, Long memberId);
    List<LoungeParticipantGetResponse> getLoungeParticipantWithLoungeIdAndMemberId(Long loungeId);

}
