package com.ssafy.moa2zi.challenge.presentation;

import com.ssafy.moa2zi.challenge.application.ChallengeService;
import com.ssafy.moa2zi.challenge.dto.request.*;
import com.ssafy.moa2zi.challenge.dto.response.*;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<ChallengeSearchResponse> getChallenges(
            ChallengeSearchRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        ChallengeSearchResponse result = challengeService.getChallenges(request, loginMember);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{challengeId}/participants")
    public ResponseEntity<ParticipantGetResponse> getParticipants(
            ParticipantGetRequest request,
            @PathVariable(name = "challengeId") Long challengeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        ParticipantGetResponse result = challengeService.getParticipants(challengeId, request, loginMember);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{challengeTimeId}")
    public ResponseEntity<Void> joinChallenge(
            @PathVariable(name = "challengeTimeId") Long challengeTimeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        challengeService.joinChallenge(challengeTimeId, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{challengeTimeId}/review")
    public ResponseEntity<Void> createReview(
            @Valid @RequestBody ChallengeReviewCreateRequest request,
            @PathVariable(name = "challengeTimeId") Long challengeTimeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        challengeService.createReview(challengeTimeId, request, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reviews/{challengeParticipantId}")
    public ResponseEntity<Void> toggleReviewLike(
            @PathVariable(name = "challengeParticipantId") Long challengeParticipantId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        challengeService.toggleReviewLike(challengeParticipantId, loginMember);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/{challengeId}")
    public ResponseEntity<ReviewGetResponse> getChallengeReviews(
            ReviewGetRequest request,
            @PathVariable(name = "challengeId") Long challengeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        ReviewGetResponse result = challengeService.getChallengeReviews(challengeId, request, loginMember);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{challengeTimeId}/award")
    public ResponseEntity<AwardInfoResponse> getAwardByChallengeTimeId(
            @PathVariable(name = "challengeTimeId") Long challengeTimeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        AwardInfoResponse result = challengeService.getAwardByChallengeTimeId(challengeTimeId, loginMember);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/award")
    public ResponseEntity<List<AwardInfoResponse>> getAwardsByMemberId(
            @RequestParam(name = "memberId") Long memberId
    ) {

        List<AwardInfoResponse> result = challengeService.getAwardsByMember(memberId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/award/read")
    public ResponseEntity<Void> readAward(
            @RequestParam(name = "challengeTimeId") Long challengeTimeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        challengeService.readAward(challengeTimeId, loginMember);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/award/images",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Void> addAwardImage(
            @RequestPart("challengeTimeId") Long challengeTimeId,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        challengeService.addAwardImage(new AwardCreateImageRequest(challengeTimeId, image), loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
