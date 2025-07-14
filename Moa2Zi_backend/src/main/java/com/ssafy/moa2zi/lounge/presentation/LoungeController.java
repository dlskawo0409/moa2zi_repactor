package com.ssafy.moa2zi.lounge.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.moa2zi.common.scheduler.MidnightScheduler;
import com.ssafy.moa2zi.day.dto.request.DayTransactionSumGetRequest;
import com.ssafy.moa2zi.lounge.application.LoungeService;
import com.ssafy.moa2zi.lounge.domain.SearchType;
import com.ssafy.moa2zi.lounge.dto.request.LoungeCreateRequest;
import com.ssafy.moa2zi.lounge.dto.request.LoungeGetRequest;
import com.ssafy.moa2zi.lounge.dto.response.LoungeDetailResponse;
import com.ssafy.moa2zi.lounge.dto.response.LoungeListResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.quiz.application.QuizService;
import com.ssafy.moa2zi.quiz.domain.Quiz;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithRankResultResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithResultSumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lounges")
public class LoungeController {

    private final LoungeService loungeService;

    @PostMapping
    public ResponseEntity<Void> createLounge(
            @Valid @RequestBody LoungeCreateRequest loungeCreateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
            ) throws BadRequestException {

        loungeService.createLounge(loungeCreateRequest, loginMember);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/lounges").toUriString());

        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<?> getLounges(
            LoungeGetRequest loungeGetRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws BadRequestException {

        if(loungeGetRequest.searchType().equals(SearchType.NICKNAME)){
            return ResponseEntity.ok(loungeService.getLoungeWithNickname(loungeGetRequest, loginMember));
        }

        return ResponseEntity.ok(loungeService.getLounge(loungeGetRequest, loginMember));

    }

    @GetMapping("{loungeId}/details")
    public ResponseEntity<LoungeDetailResponse> getLoungeDetails(
            @PathVariable(name = "loungeId") Long loungeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws BadRequestException, AccessDeniedException {

        return ResponseEntity.ok(loungeService.getLoungeDetails(loungeId,loginMember));
    }


}
