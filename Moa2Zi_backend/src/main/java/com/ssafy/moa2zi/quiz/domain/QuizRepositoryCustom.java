package com.ssafy.moa2zi.quiz.domain;

import com.ssafy.moa2zi.quiz.dto.response.QuizWithRankResultResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithResultResponse;

import java.util.List;

public interface QuizRepositoryCustom {

    List<QuizWithResultResponse> getQuizWithResult(Long gameId, Long memberId);
    QuizWithRankResultResponse getQuizWithRanking(
            Long gameId,
            Long memberId,
            List<Long> participantIdList
    );
}
