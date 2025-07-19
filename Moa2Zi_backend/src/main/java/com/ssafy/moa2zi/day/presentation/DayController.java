package com.ssafy.moa2zi.day.presentation;

import com.ssafy.moa2zi.day.application.DayService;
import com.ssafy.moa2zi.day.dto.request.DayCommentCreateRequest;
import com.ssafy.moa2zi.day.dto.request.DayCommentSearchRequest;
import com.ssafy.moa2zi.day.dto.response.DayCommentSearchResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/days")
public class DayController {

    private final DayService dayService;

    @PostMapping("/{dayId}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable(name = "dayId") Long dayId,
            @Valid @RequestBody DayCommentCreateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        dayService.createComment(dayId, request, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{dayId}/comments")
    public ResponseEntity<DayCommentSearchResponse> getComments(
            @PathVariable(name = "dayId") Long dayId,
            DayCommentSearchRequest request
    ) {

        DayCommentSearchResponse result = dayService.getComments(dayId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        dayService.deleteComment(commentId, loginMember);
        return ResponseEntity.noContent().build();
    }

}
