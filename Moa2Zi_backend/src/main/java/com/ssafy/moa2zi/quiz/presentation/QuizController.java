package com.ssafy.moa2zi.quiz.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.moa2zi.common.scheduler.MidnightScheduler;
import com.ssafy.moa2zi.day.dto.request.DayTransactionSumGetRequest;
import com.ssafy.moa2zi.day.dto.request.SumWithCategory;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.quiz.application.QuizService;
import com.ssafy.moa2zi.quiz.domain.Quiz;
import com.ssafy.moa2zi.quiz.dto.response.QuizGetResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithRankResultResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithResultSumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@Slf4j
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final MidnightScheduler midnightScheduler;

    @PostMapping
    public ResponseEntity<?> createQuiz(
            DayTransactionSumGetRequest dayTransactionSumGetRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws BadRequestException, JsonProcessingException {
        return ResponseEntity.ok(quizService.createQuiz(dayTransactionSumGetRequest, loginMember));
    }


    @PostMapping("/midnight")
    public void createQuizWitMidNight(){
        midnightScheduler.runAtMidnight();
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizGetResponse> getQuiz(@PathVariable(name = "quizId") Long quizId){
        return ResponseEntity.ok(quizService.getQuiz(quizId));
    }

    @GetMapping("/result")
    public ResponseEntity<QuizWithResultSumResponse> getQuizWithResult(
            @RequestParam Long gameId,
            @RequestParam Long memberId
    ){
        return ResponseEntity.ok(quizService.getQuizWithResult(gameId, memberId));
    }

    @GetMapping("/ranking")
    public ResponseEntity<QuizWithRankResultResponse> getQuizWithRanking(
            @RequestParam Long gameId,
            @RequestParam Long memberId
    ){
        return ResponseEntity.ok(quizService.getQuizWithRanking(gameId, memberId));
    }



}
