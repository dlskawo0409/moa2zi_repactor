package com.ssafy.moa2zi.finance.presentation;

import com.ssafy.moa2zi.finance.application.FinanceAdminService;
import com.ssafy.moa2zi.finance.dto.card.*;
import com.ssafy.moa2zi.finance.dto.deposit.DepositCreateResponse;
import com.ssafy.moa2zi.finance.dto.deposit.DepositCreateRequest;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountCreateResponse;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountCreateRequest;
import com.ssafy.moa2zi.finance.dto.deposit.DepositListGetResponse;
import com.ssafy.moa2zi.finance.dto.member.MemberCardInfo;
import com.ssafy.moa2zi.finance.dto.merchant.CategoryGetResponse;
import com.ssafy.moa2zi.finance.dto.merchant.MerchantRegisterRequest;
import com.ssafy.moa2zi.finance.dto.merchant.MerchantGetResponse;
import com.ssafy.moa2zi.finance.dto.transaction.TransactionHistoryRequest;
import com.ssafy.moa2zi.finance.dto.transaction.TransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fin/admin")
public class FinanceAdminController {

    private final FinanceAdminService financeAdminService;


    /*
        수시입출금 상품 목록 조회 (계좌 생성 대상)
     */
    @GetMapping("/deposit")
    public ResponseEntity<List<DepositListGetResponse>> getDepositList() {
        List<DepositListGetResponse> result = financeAdminService.getDepositList();
        return ResponseEntity.ok(result);
    }

    /*
        사용자 계좌 생성 (더미 데이터 만들기)
     */
    @PostMapping("/depositAccount")
    public ResponseEntity<MemberAccountCreateResponse> createDepositAccount(
            @RequestBody MemberAccountCreateRequest request,
            @RequestParam(name = "memberId") Long memberId
    ) throws Exception {

        MemberAccountCreateResponse result = financeAdminService.createDepositAccount(request, memberId);
        return ResponseEntity.ok(result);
    }

    /*
        수시입출금 상품 등록 (계좌 생성 대상)
     */
    @PostMapping("/deposit")
    public ResponseEntity<DepositCreateResponse> createDeposit(
            @RequestBody DepositCreateRequest request
    ) {

        DepositCreateResponse result = financeAdminService.createDeposit(request);
        return ResponseEntity.ok(result);
    }

    /*
        사용자 계좌 거래 내역 조회
     */
    @PostMapping("/transactionHistory")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistoryList(
            @RequestBody TransactionHistoryRequest request,
            @RequestParam(name = "memberId") Long memberId
    ) throws Exception {

        TransactionHistoryResponse result = financeAdminService.getTransactionHistory(request, memberId);
        return ResponseEntity.ok(result);
    }

    /*
     가맹점 등록을 위한 카테고리 조회
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryGetResponse>> getCategoryList() {

        List<CategoryGetResponse> result = financeAdminService.getCategoryList();
        return ResponseEntity.ok(result);
    }

    /*
        가맹점 등록하기
     */
    @PostMapping("/merchant")
    public ResponseEntity<List<MerchantGetResponse>> registerMerchant(
            @RequestBody MerchantRegisterRequest request
    ) {

        List<MerchantGetResponse> result = financeAdminService.registerMerchant(request);
        return ResponseEntity.ok(result);
    }

    /*
        가맹점 목록 조회
     */
    @GetMapping("/merchant")
    public ResponseEntity<List<MerchantGetResponse>> getMerchantList() {

        List<MerchantGetResponse> result = financeAdminService.getMerchantList();
        return ResponseEntity.ok(result);
    }

    /*
        카드 상품 조회
     */
    @GetMapping("/cardProduct")
    public ResponseEntity<List<CardProductGetResponse>> getCardProductList() {

        List<CardProductGetResponse> result = financeAdminService.getCardProductList();
        return ResponseEntity.ok(result);
    }

    /*
        카드 상품 등록
     */
    @PostMapping("/cardProduct")
    public ResponseEntity<CardProductGetResponse> createCardProduct(
            @RequestBody CardProductCreateRequest request
    ) {

        CardProductGetResponse result = financeAdminService.createCardProduct(request);
        return ResponseEntity.ok(result);
    }

    /*
        사용자 카드 생성
     */
    @PostMapping("/creditCard")
    public ResponseEntity<MemberCardInfo> createCreditCard(
            @RequestParam(name = "memberId") Long memberId,
            @RequestBody CardCreateRequest request
    ) throws Exception {

        MemberCardInfo result = financeAdminService.createCreditCard(memberId, request);
        return ResponseEntity.ok(result);
    }

    /*
        사용자 카드 목록 조회
     */
    @GetMapping("/creditCard")
    public ResponseEntity<List<MemberCardInfo>> getCreditCardListByMember(
            @RequestParam(name = "memberId") Long memberId
    ) throws Exception {

        List<MemberCardInfo> result = financeAdminService.getCreditCardListByMember(memberId);
        return ResponseEntity.ok(result);
    }

    /*
        카드 결제
     */
    @PostMapping("/creditCard/transaction")
    public ResponseEntity<CardTransactionCreateResponse> createCardTransaction(
            @RequestParam(name = "memberId") Long memberId,
            @RequestBody CardTransactionCreateRequest request
    ) throws Exception {

        CardTransactionCreateResponse result = financeAdminService.createCardTransaction(memberId, request);
        return ResponseEntity.ok(result);
    }

    /*
        사용자 카드 결제 내역 조회
     */
    @GetMapping("/creditCard/transaction")
    public ResponseEntity<CardTransactionGetResponse> getCardTransactionByMember(
            @RequestParam(name = "memberId") Long memberId,
            @RequestBody CardTransactionGetRequest request
    ) throws Exception {

        CardTransactionGetResponse result = financeAdminService.getCardTransactionByMember(memberId, request);
        return ResponseEntity.ok(result);
    }

}

