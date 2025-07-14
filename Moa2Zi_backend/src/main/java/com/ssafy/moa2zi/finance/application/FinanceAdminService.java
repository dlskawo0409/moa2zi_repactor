package com.ssafy.moa2zi.finance.application;

import com.amazonaws.handlers.IRequestHandler2;
import com.ssafy.moa2zi.finance.dto.ApiResponse;
import com.ssafy.moa2zi.finance.dto.ApiRequest;
import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestArgs;
import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;
import com.ssafy.moa2zi.common.infrastructure.finopenapi.FinOpenApiClient;
import com.ssafy.moa2zi.finance.dto.card.*;
import com.ssafy.moa2zi.finance.dto.deposit.*;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountCreateApiRequest;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountCreateResponse;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountCreateRequest;
import com.ssafy.moa2zi.finance.dto.member.MemberCardInfo;
import com.ssafy.moa2zi.finance.dto.merchant.CategoryGetResponse;
import com.ssafy.moa2zi.finance.dto.merchant.MerchantRegisterApiRequest;
import com.ssafy.moa2zi.finance.dto.merchant.MerchantRegisterRequest;
import com.ssafy.moa2zi.finance.dto.merchant.MerchantGetResponse;
import com.ssafy.moa2zi.finance.dto.transaction.TransactionHistoryApiRequest;
import com.ssafy.moa2zi.finance.dto.transaction.TransactionHistoryRequest;
import com.ssafy.moa2zi.finance.dto.transaction.TransactionHistoryResponse;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FinanceAdminService {

    private final MemberRepository memberRepository;
    @Value("${fin-open-api.api-key}")
    private String apiKey;

    @Value("${fin-open-api.admin-user-key}")
    private String adminUserKey;

    private final FinOpenApiClient finOpenApiClient;


    /**
     * 수시입출금 상품 목록 전체 조회
     */
    public List<DepositListGetResponse> getDepositList() {

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireDemandDepositList")
                .apiServiceCode("inquireDemandDepositList")
                .apiKey(apiKey)
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                        "/edu/demandDeposit/inquireDemandDepositList",
                        request,
                        new ParameterizedTypeReference<ApiResponse<List<DepositListGetResponse>>>() {})
                .REC();
    }

    /**
     * 사용자 계좌 생성
     */
    public MemberAccountCreateResponse createDepositAccount(
            MemberAccountCreateRequest request,
            Long memberId
    ) throws Exception {

        Member member = findMemberById(memberId);
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("createDemandDepositAccount")
                .apiServiceCode("createDemandDepositAccount")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        MemberAccountCreateApiRequest apiRequest = MemberAccountCreateApiRequest.from(
                RequestHeader.args(args),
                request.accountTypeUniqueNo()
        );

        return finOpenApiClient.post(
                        "/edu/demandDeposit/createDemandDepositAccount",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<MemberAccountCreateResponse>>() {})
                .REC();
    }

    /**
     * 계좌 상품 등록
     */
    public DepositCreateResponse createDeposit(
            DepositCreateRequest request
    ) {

        RequestArgs args = RequestArgs.builder()
                .apiName("createDemandDeposit")
                .apiServiceCode("createDemandDeposit")
                .apiKey(apiKey)
                .build();

        DepositCreateApiRequest apiRequest = DepositCreateApiRequest.from(
                RequestHeader.args(args),
                request
        );

        return finOpenApiClient.post(
                        "/edu/demandDeposit/createDemandDeposit",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<DepositCreateResponse>>() {})
                .REC();
    }

    /**
     * 계좌 거래 내역 조회
     */
    public TransactionHistoryResponse getTransactionHistory (
            TransactionHistoryRequest request,
            Long memberId
    ) throws Exception {

        Member member = findMemberById(memberId);
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireTransactionHistoryList")
                .apiServiceCode("inquireTransactionHistoryList")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        TransactionHistoryApiRequest apiRequest = TransactionHistoryApiRequest.from(
                RequestHeader.args(args),
                request
        );

        return finOpenApiClient.post(
                        "/edu/demandDeposit/inquireTransactionHistoryList",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<TransactionHistoryResponse>>() {})
                .REC();
    }

    /**
     * 카테고리 조회 (가맹점 등록하려면 필요)
     */
    public List<CategoryGetResponse> getCategoryList() {

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireCategoryList")
                .apiServiceCode("inquireCategoryList")
                .apiKey(apiKey)
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                "/edu/creditCard/inquireCategoryList",
                request,
                new ParameterizedTypeReference<ApiResponse<List<CategoryGetResponse>>>() {})
                .REC();
    }

    /**
     * 가맹점 등록
     */
    public List<MerchantGetResponse> registerMerchant(
            MerchantRegisterRequest request
    ) {

        RequestArgs args = RequestArgs.builder()
                .apiName("createMerchant")
                .apiServiceCode("createMerchant")
                .apiKey(apiKey)
                .build();

        MerchantRegisterApiRequest apiRequest = MerchantRegisterApiRequest.from(RequestHeader.args(args), request);

        System.out.println(apiRequest);
        return finOpenApiClient.post(
                "/edu/creditCard/createMerchant",
                apiRequest,
                new ParameterizedTypeReference<ApiResponse<List<MerchantGetResponse>>>() {}).REC();
    }

    /**
     * 가맹점 목록 조회
     */
    public List<MerchantGetResponse> getMerchantList() {

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireMerchantList")
                .apiServiceCode("inquireMerchantList")
                .apiKey(apiKey)
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                "/edu/creditCard/inquireMerchantList",
                request,
                new ParameterizedTypeReference<ApiResponse<List<MerchantGetResponse>>>() {})
                .REC();
    }


    /**
     * 카드 상품 조회 (카드 생성 시 필요)
     */
    public List<CardProductGetResponse> getCardProductList() {

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireCreditCardList")
                .apiServiceCode("inquireCreditCardList")
                .apiKey(apiKey)
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                        "/edu/creditCard/inquireCreditCardList",
                        request,
                        new ParameterizedTypeReference<ApiResponse<List<CardProductGetResponse>>>() {})
                .REC();
    }

    /**
     * 카드 상품 등록
     */
    public CardProductGetResponse createCardProduct(
            CardProductCreateRequest request
    ) {

        RequestArgs args = RequestArgs.builder()
                .apiName("createCreditCardProduct")
                .apiServiceCode("createCreditCardProduct")
                .apiKey(apiKey)
                .build();

        CardProductCreateApiRequest apiRequest = CardProductCreateApiRequest.from(RequestHeader.args(args), request);

        return finOpenApiClient.post(
                        "/edu/creditCard/createCreditCardProduct",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<CardProductGetResponse>>() {})
                .REC();
    }

    /**
     * 사용자 카드 생성
     */
    public MemberCardInfo createCreditCard(
            Long memberId,
            CardCreateRequest request
    ) throws Exception {

        Member member = findMemberById(memberId);
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("createCreditCard")
                .apiServiceCode("createCreditCard")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        CardCreateApiRequest apiRequest = CardCreateApiRequest.from(RequestHeader.args(args), request);

        return finOpenApiClient.post(
                        "/edu/creditCard/createCreditCard",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<MemberCardInfo>>() {})
                .REC();
    }

    /**
     * 사용자 카드 목록 조회
     */
    public List<MemberCardInfo> getCreditCardListByMember(
            Long memberId
    ) throws Exception {

        Member member = findMemberById(memberId);
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireSignUpCreditCardList")
                .apiServiceCode("inquireSignUpCreditCardList")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                        "/edu/creditCard/inquireSignUpCreditCardList",
                        request,
                        new ParameterizedTypeReference<ApiResponse<List<MemberCardInfo>>>() {})
                .REC();
    }

    /**
     * 카드 결제
     */
    public CardTransactionCreateResponse createCardTransaction(
            Long memberId,
            CardTransactionCreateRequest request
    ) throws Exception {

        Member member = findMemberById(memberId);
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("createCreditCardTransaction")
                .apiServiceCode("createCreditCardTransaction")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        CardTransactionCreateApiRequest apiRequest = CardTransactionCreateApiRequest.from(RequestHeader.args(args), request);

        return finOpenApiClient.post(
                        "/edu/creditCard/createCreditCardTransaction",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<CardTransactionCreateResponse>>() {})
                .REC();
    }

    /**
     * 카드 결제 내역 조회
     */
    public CardTransactionGetResponse getCardTransactionByMember(
            Long memberId,
            CardTransactionGetRequest request
    ) throws Exception {

        Member member = findMemberById(memberId);
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireCreditCardTransactionList")
                .apiServiceCode("inquireCreditCardTransactionList")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        CardTransactionGetApiRequest apiRequest = CardTransactionGetApiRequest.from(RequestHeader.args(args), request);

        return finOpenApiClient.post(
                        "/edu/creditCard/inquireCreditCardTransactionList",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<CardTransactionGetResponse>>() {})
                .REC();
    }


    private void validateMemberHasUserKey(Member member) throws IllegalAccessException {
        if(Objects.isNull(member.getUserKey())) {
            throw new IllegalAccessException("해당 ID의 유저는 USER KEY 를 가지고 있지 않습니다, " + member.getMemberId());
        }
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 유저가 존재하지 않습니다, " + memberId));
    }

}
