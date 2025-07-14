package com.ssafy.moa2zi.game.application;

import com.ssafy.moa2zi.game.domain.Game;
import com.ssafy.moa2zi.game.domain.GameRepository;
import com.ssafy.moa2zi.game.dto.response.GameGetResponse;
import com.ssafy.moa2zi.game.dto.response.GameScoreRanking;
import com.ssafy.moa2zi.lounge.domain.Lounge;
import com.ssafy.moa2zi.lounge.domain.LoungeRepository;

import com.ssafy.moa2zi.lounge_participant.domain.LoungeParticipantRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final LoungeRepository loungeRepository;
    private final LoungeParticipantRepository loungeParticipantRepository;
    private final GameRepository gameRepository;

    public void createGame(){

        LocalDateTime today = LocalDateTime.now();

        List<Lounge> loungeList = loungeRepository.getLoungeListLargeThanEndTime(today);

        List<Long> createLoungeIdList = loungeList.stream()
                .filter(l -> {
                    LocalDate createdDate = l.getCreatedAt().toLocalDate();
                    long daysBetween = ChronoUnit.DAYS.between(createdDate, today);
                    return l.getDuration() >= daysBetween && daysBetween % l.getDuration() == 0;
                })
                .map(Lounge::getId)
                .toList();

        List<Game> newGameList = createLoungeIdList.stream()
                .map(l -> Game.builder()
                        .loungeId(l)
                        .createdAt(today)
                        .endTime(today.plusDays(1)) // 오늘 + 1일
                        .build())
                .toList();

        gameRepository.bulkInsert(newGameList);

    }

    public List<GameGetResponse> getGame(Long loungeId, CustomMemberDetails loginMember) throws AccessDeniedException {

        if(!loungeParticipantRepository.existsByMemberIdAndLoungeId(loginMember.getMemberId(), loungeId)){
            throw new AccessDeniedException("라운지 참가 권한이 없습니다.");
        }

        return gameRepository.getGameListWith(loungeId, loginMember.getMemberId());
    }

    public List<GameScoreRanking> getGameHistory(CustomMemberDetails loginMember){
        return gameRepository.getGameHistory(loginMember);
    }


}
