package com.ssafy.moa2zi.lounge.domain;

import com.ssafy.moa2zi.lounge.dto.request.LoungeGetRequest;
import com.ssafy.moa2zi.lounge.dto.response.LoungeWithGame;
import com.ssafy.moa2zi.lounge.dto.response.LoungeWithGameAndParticipant;
import com.ssafy.moa2zi.lounge.dto.response.LoungeWithParticipant;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoungeRepositoryCustom {

//    List<LoungeWithGame> getLoungeWithGame(
//        int size,
//        LocalDateTime today,
//        LoungeGetRequest loungeGetRequest,
//        Long memberId
//    );

    List<LoungeWithGame> getLoungeWithGame(
            int size,
            LoungeGetRequest loungeGetRequest,
            LocalDateTime now,
            Long memberId
    );

    List<LoungeWithParticipant> getLoungeWithParticipantByLoungeIdList(List<Long> loungeIdList);

    Long getTotal(
        LoungeGetRequest loungeGetRequest,
        Long memberId
    );

    List<Lounge> getLoungeListLargeThanEndTime(LocalDateTime dateTime);
    List<Long> getLoungeIdListByMemberId(Long memberId);
    List<LoungeWithGameAndParticipant> getLoungeWithGameAndParticipantListByLoungeIdAndMemberId(
            LoungeGetRequest loungeGetRequestWithNickname,
            List<Long> loungeIdList,
            List<Long> memberIdList,
            Long memberId
    );
    Long getLoungeWithNicknameTotal(List<Long> loungeIdList, List<Long> memberIdList);

    Optional<LoungeWithGame> getLoungeByGameId(Long gameId);
    Long getLoungeIdByGameId(Long gameId);

}
