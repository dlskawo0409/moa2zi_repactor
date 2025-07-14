package com.ssafy.moa2zi.transaction.presentation;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.notification.dto.response.NotificationResponse;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import com.ssafy.moa2zi.transaction.dto.request.*;

import com.ssafy.moa2zi.transaction.dto.response.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
@Tag(name = "Transaction", description = "거래내역 API")
public class TransactionController {

    private final TransactionService transactionService;

    @DeleteMapping
    @Operation(summary = "가계부 요소 삭제", description = "가계부 요소를 삭제합니다.")
    public ResponseEntity<Void> deleteTransaction (
            @RequestBody Long transactionId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        transactionService.deleteTransaction(transactionId, loginMember);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/emotion")
    @Operation(summary = "기분 등록하기", description = "거래 내역에서 기분을 등록합니다.")
    public ResponseEntity<Void> createTransactionEmotion(
            @RequestBody EmotionCreateRequest ecr,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        transactionService.createTransactionEmotion(ecr, loginMember);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sum/category")
    @Operation(summary = "카테고리별 분석 가져오기", description = "카테고리별 분석 결과를 가져옵니다.")
    public ResponseEntity<List<AnalysisByCategorySearchResponse>> getAnalysisByCategory(
            @ModelAttribute AnalysisByCategoryListSearchRequest abclr,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {
        return ResponseEntity.ok(transactionService.getAnalysisByCategory(abclr, loginMember));
    }

    @GetMapping("/sum")
    @Operation(summary = "총 지출 금액 가져오기", description = "월별 총 지출 금액을 가져옵니다.")
    public ResponseEntity<List<MonthlySpendSumResponse>> getTotalSpendSumList(
            @ModelAttribute MonthlySpendSumListSearchRequest msslsr,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(transactionService.getTotalSpendSumList(msslsr, loginMember));
    }

    @GetMapping("/day")
    @Operation(summary = "가계부 일별 리스트 가져오기", description = "요청한 transactionDate 에 해당하는 가계부 일별 리스트를 가져옵니다.")
    public ResponseEntity<DailyTransactionListSearchResponse> getDailyTransactionList(
            @RequestParam @NotNull Long memberId,
            @RequestParam @NotNull Integer transactionDate,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(transactionService.getDailyTransactionList(memberId, transactionDate, loginMember));
    }

    @GetMapping("/calender")
    @Operation(summary = "가계부 달력 가져오기", description = "요청한 transactionDate 에 해당하는 가계부 달력 데이터를 가져옵니다.")
    public ResponseEntity<TransactionCalenderSearchResponse> getTransactionCalender(
            @RequestParam @NotNull Long memberId,
            @RequestParam @NotNull Integer transactionDate,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(transactionService.getTransactionCalender(memberId, transactionDate, loginMember));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "가계부 상세 가져오기", description = "요청한 transactionId 에 해당하는 거래 상세 내역을 가져옵니다.")
    public ResponseEntity<TransactionDetailSearchResponse> getTransactionDetail(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @PathVariable Long transactionId
    ) throws Exception {
        return ResponseEntity.ok(transactionService.getTransactionDetail(loginMember, transactionId));
    }

    @GetMapping
    @Operation(summary = "가계부 리스트 가져오기", description = "가계부 리스트를 가져옵니다.")
    public ResponseEntity<TransactionListSearchResponse> getTransactionList (
            @ModelAttribute TransactionListSearchRequest transactionListSearchRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {
        TransactionListSearchResponse TransactionListSearchResponse = transactionService.getTransactionList(loginMember, transactionListSearchRequest);
        return ResponseEntity.ok(TransactionListSearchResponse);
    }

    @PostMapping
    @Operation(summary = "가계부 수기입력", description = "수입과 지출 내역을 수기로 입력합니다.")
    public ResponseEntity<Void> createTransactionManually (
            @RequestBody @Valid TransactionCreateRequest transactionCreateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {
        transactionService.createTransactionManually(loginMember, transactionCreateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/map")
    public ResponseEntity<TransactionSearchResponse> getTransactions(
            TransactionSearchRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        TransactionSearchResponse result = transactionService.findTransactions(request, loginMember);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/map/clusters")
    public ResponseEntity<List<MapClusterResponse>> getClustersByMapSearch (
            MapClusterRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        List<MapClusterResponse> result = transactionService.getClustersByMapSearch(request, loginMember);
        return ResponseEntity.ok(result);
    }

    /**
     * polling 방식
     * 위치 기반 알림
     */
//    @GetMapping("/location/alert")
//    public ResponseEntity<List<NotificationResponse>> checkAlertByLocation(
//            @RequestParam float latitude,
//            @RequestParam float longitude,
//            @AuthenticationPrincipal CustomMemberDetails loginMember
//    ) {
//
//        List<NotificationResponse> result = transactionService.checkAlertByLocation(latitude, longitude, loginMember);
//        return ResponseEntity.ok(result);
//    }

}
