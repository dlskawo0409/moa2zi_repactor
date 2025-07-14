package com.ssafy.moa2zi.finance.presentation;

import com.ssafy.moa2zi.finance.application.FinanceMemberService;
import com.ssafy.moa2zi.finance.dto.asset.AssetConnectRequest;
import com.ssafy.moa2zi.finance.dto.asset.AssetListResponse;
import com.ssafy.moa2zi.finance.dto.asset.FetchedAssetResponse;
import com.ssafy.moa2zi.finance.dto.auth.*;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountInfo;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fin")
public class FinanceMemberController {

    private final FinanceMemberService financeMemberService;


    @GetMapping("/assets")
    public ResponseEntity<AssetListResponse> getAssetList() {
        AssetListResponse result = financeMemberService.getAssetList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/member/accounts")
    public ResponseEntity<List<MemberAccountInfo>> getMemberAccountList(
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {
        List<MemberAccountInfo> result = financeMemberService.getMemberAccountList(loginMember);
        return ResponseEntity.ok(result);
    }

    /*
        선택한 은행, 카드사 등록 (캐싱)
     */
    @PostMapping("/assets")
    public ResponseEntity<Void> registerSelectedAsset(
            @RequestBody AssetConnectRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        financeMemberService.cacheSelectedAsset(request, loginMember);
        return ResponseEntity.ok().build();
    }

    /*
        1원 송금
     */
    @PostMapping("/accountAuth")
    public ResponseEntity<Void> requestAccountAuth(
            @RequestBody OpenAccountAuthRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        financeMemberService.requestOpenAccountAuth(request, loginMember);
        return ResponseEntity.ok().build();
    }

    /*
        1원 송금 검증
     */
    @PostMapping("/accountAuth/check")
    public ResponseEntity<CheckAuthResponse> checkAuthCode(
            @RequestBody CheckAuthRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        CheckAuthResponse result = financeMemberService.checkOpenAccountAuth(request, loginMember);
        return ResponseEntity.ok(result);
    }

    /*
        탐색된 자산 가져오기
     */
    @GetMapping("/assets/fetch")
    public ResponseEntity<FetchedAssetResponse> fetchAssetsByMember(
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        FetchedAssetResponse result = financeMemberService.fetchAssets(loginMember);
        return ResponseEntity.ok(result);
    }

}
