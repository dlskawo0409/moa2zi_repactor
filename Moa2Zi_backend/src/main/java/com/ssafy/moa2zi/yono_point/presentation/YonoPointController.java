package com.ssafy.moa2zi.yono_point.presentation;

import com.ssafy.moa2zi.common.scheduler.YonoPointScheduler;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.yono_point.application.YonoPointService;
import com.ssafy.moa2zi.yono_point.dto.response.YonoPointSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/yono-points")
@Tag(name = "YonoPoint", description = "yono 포인트 API")
public class YonoPointController {

    private final YonoPointScheduler yonoPointScheduler;
    private final YonoPointService yonoPointService;

    @GetMapping
    @Operation(summary = "Yono Point 가져오기", description = "Yono Point 정보를 가져옵니다.")
    public ResponseEntity<YonoPointSearchResponse> getYonoPoint(
            @RequestParam(name="transactionDate") Integer transactionDate,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(yonoPointService.getYonoPoint(transactionDate, loginMember));
    }

    @PostMapping("/batchYonoPoint")
    public void batchYonoPoint(){
        yonoPointScheduler.batchYonoPoint();
    }
}
