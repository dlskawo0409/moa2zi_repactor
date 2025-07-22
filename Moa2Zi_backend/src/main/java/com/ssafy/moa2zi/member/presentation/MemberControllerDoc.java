package com.ssafy.moa2zi.member.presentation;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.member.dto.request.MemberJoinRequest;
import com.ssafy.moa2zi.member.dto.request.MemberUpdateRequest;
import com.ssafy.moa2zi.member.dto.request.MemberUsernameDuplicateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDoc {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 OK!!"),
            @ApiResponse(responseCode = "400", description = "이메일/닉네임 중복 등"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> join(@Valid @RequestBody MemberJoinRequest memberJoinRequest) throws Exception;

    @Operation(summary = "이메일 중복 확인", description = "입력된 이메일의 중복 여부를 확인합니다.")
    public ResponseEntity<?> checkEmail(
            @Valid @RequestBody MemberUsernameDuplicateRequest memberUsernameDuplicateRequest);

    @Operation(summary = "로그인 본인 회원 정보 확인", description = "로그인한 본인 회원 정보를 확인합니다.")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal CustomMemberDetails loginMember);

    @Operation(summary = "회원 정보 확인", description = "회원 정보를 확인합니다.")
    public ResponseEntity<?> getMember(
            @PathVariable(name = "member-id") Long memberId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    );

    @Operation(summary = "회원 정보 수정", description = "프로필 이미지를 제외한 정보를 수정합니다.")
    public ResponseEntity<?> updateMember(
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    );
}
