package com.ssafy.moa2zi.pocket_money.presentation;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.pocket_money.application.PocketMoneyService;
import com.ssafy.moa2zi.pocket_money.dto.request.PocketMoneyCreateRequest;
import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneySearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pocket-monies")
@Tag(name = "PocketMoney", description = "셀프 용돈 API")
public class PocketMoneyController {

    private final PocketMoneyService pocketMoneyService;

    @GetMapping
    @Operation(summary = "셀프 용돈 가져오기", description = "셀프 용돈의 정보를 가져옵니다.")
    public ResponseEntity<PocketMoneySearchResponse> getPocketMoneyInfo(
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(pocketMoneyService.getPocketMoneyInfo(loginMember));
    }

    @PostMapping
    @Operation(summary = "셀프 용돈 설정", description = "셀프 용돈 금액을 설정합니다.")
    public ResponseEntity<Void> setPocketMoney(
            @RequestBody @Valid PocketMoneyCreateRequest pmcr,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        pocketMoneyService.setPocketMoney(pmcr, loginMember);
        return ResponseEntity.ok().build();
    }
}
