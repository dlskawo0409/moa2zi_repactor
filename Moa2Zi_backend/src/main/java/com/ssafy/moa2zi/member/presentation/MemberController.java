package com.ssafy.moa2zi.member.presentation;

import com.ssafy.moa2zi.member.application.MemberService;
import com.ssafy.moa2zi.member.dto.request.*;
import com.ssafy.moa2zi.member.dto.response.MemberGetByNicknameListResponse;
import com.ssafy.moa2zi.member.dto.response.MemberUsernameDuplicateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원관리 API")
public class MemberController implements MemberControllerDoc {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> join(@Valid @RequestBody MemberJoinRequest memberJoinRequest) throws Exception {
        memberService.createMember(memberJoinRequest);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(
            @Valid @RequestBody MemberUsernameDuplicateRequest memberUsernameDuplicateRequest
    ) {
        memberService.duplicationUsernameWithThrow(memberUsernameDuplicateRequest.username());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(
            @Valid @RequestBody MemberNickNameRequest memberNickNameRequest
    ){
        memberService.duplicationNicknameWithThrow(memberNickNameRequest.nickname());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getMember(@AuthenticationPrincipal CustomMemberDetails loginMember) {
        return ResponseEntity.ok(memberService.getMember(loginMember));
    }

    // 회원 정보 조회 (member-id)
    @GetMapping("/{memberId}")
    public ResponseEntity<?> getMember(
            @PathVariable(name = "memberId") Long memberId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(memberService.getMember(memberId, loginMember));
    }

    @GetMapping("/nickname")
    public ResponseEntity<MemberGetByNicknameListResponse> getMemberByNickname(
            @Valid @ModelAttribute MemberGetByNicknameRequest memberGetByNicknameRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws BadRequestException {

        return ResponseEntity.ok(memberService.getMemberByNickname(memberGetByNicknameRequest, loginMember));
    }


    @PutMapping
    public ResponseEntity<?> updateMember(
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ){
        memberService.updateMember(memberUpdateRequest, loginMember);
        return ResponseEntity.ok().build();
    }

//    @PatchMapping("/nickname")
//    @Operation(summary = "닉네임 변경", description = "회원의 닉네임을 변경합니다.")
//    public ResponseEntity<?> updateNickname(
//            @Valid @RequestBody MemberNickNameRequest request,
//            @AuthenticationPrincipal CustomMemberDetails loginMember
//    ) {
//        memberService.updateNickname(request, loginMember);
//        return ResponseEntity.ok().build();
//    }

    // 프로필 이미지 수정 (Multipart 요청)
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 수정", description = "회원의 프로필 이미지를 수정합니다.")
    public ResponseEntity<?> updateProfileImage(
            @RequestPart("image") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws IOException {
        memberService.updateProfileImage(loginMember.getMemberId(), multipartFile, loginMember);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 수정", description = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody MemberPasswordUpdateRequest passwordUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws BadRequestException {
        memberService.updatePassword(passwordUpdateRequest, loginMember);
        return ResponseEntity.ok().build();
    }

    // 회원 삭제
    @DeleteMapping
    public ResponseEntity<?> removeMember(@AuthenticationPrincipal CustomMemberDetails loginMember) {
        memberService.deleteMember(loginMember);
        return ResponseEntity.noContent().build();
    }
}
