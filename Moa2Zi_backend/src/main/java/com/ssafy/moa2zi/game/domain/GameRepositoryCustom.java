package com.ssafy.moa2zi.game.domain;

import com.ssafy.moa2zi.game.dto.response.GameGetResponse;
import com.ssafy.moa2zi.game.dto.response.GameScoreRanking;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;

import java.util.List;

public interface GameRepositoryCustom {
    List<GameGetResponse> getGameListWith(Long loungeId, Long memberId);
    List<GameScoreRanking> getGameHistory(CustomMemberDetails loginMember);
}
