package com.ssafy.moa2zi.finance.application;

import com.ssafy.moa2zi.account.domain.Account;
import com.ssafy.moa2zi.account.domain.AccountRepository;
import com.ssafy.moa2zi.card.domain.Card;
import com.ssafy.moa2zi.card.domain.CardRepository;
import com.ssafy.moa2zi.common.infrastructure.finopenapi.FinOpenApiClient;
import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestArgs;
import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;
import com.ssafy.moa2zi.common.util.AESUtil;
import com.ssafy.moa2zi.common.util.MaskingUtil;
import com.ssafy.moa2zi.finance.dto.ApiRequest;
import com.ssafy.moa2zi.finance.dto.ApiResponse;
import com.ssafy.moa2zi.finance.dto.asset.*;
import com.ssafy.moa2zi.finance.dto.auth.*;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountInfo;
import com.ssafy.moa2zi.finance.dto.member.MemberCardInfo;
import com.ssafy.moa2zi.finance.dto.member.MemberUserKeyRequest;
import com.ssafy.moa2zi.finance.dto.member.MemberUserKeyResponse;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceMemberService {

    @Value("${fin-open-api.api-key}")
    private String apiKey;

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    private final FinOpenApiClient finOpenApiClient;
    private final FinanceCacheService financeCacheService;
    private final AESUtil aesUtil;

    /**
     * 회원가입 시 동기 처리
     * 거래 내역 연동을 위해 금융망에서 사용자 userKey 발급
     */
    public void generateFinUserKey(Member member) {

        MemberUserKeyRequest request = MemberUserKeyRequest.builder()
                .apiKey(apiKey)
                .userId(member.getUsername()) // 이메일
                .build();

        MemberUserKeyResponse response = finOpenApiClient.post(
                "/member",
                request,
                new ParameterizedTypeReference<MemberUserKeyResponse>() {});

        member.updateUserKey(response.userKey());
    }

    /**
     * 연결 가능한 은행, 카드사 전체 조회
     */
    public AssetListResponse getAssetList() {

        List<BankInfoResponse> bankList = getBankList();
        List<CardIssuerInfoResponse> cardIssuerList = getCardList();
        return AssetListResponse.from(bankList, cardIssuerList);
    }

    public List<BankInfoResponse> getBankList() {

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireBankCodes")
                .apiServiceCode("inquireBankCodes")
                .apiKey(apiKey)
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                        "/edu/bank/inquireBankCodes",
                        request,
                        new ParameterizedTypeReference<ApiResponse<List<BankInfoResponse>>>() {})
                .REC();
    }

    public List<CardIssuerInfoResponse> getCardList() {

        RequestArgs args = RequestArgs.builder()
                .apiName("inquireCardIssuerCodesList")
                .apiServiceCode("inquireCardIssuerCodesList")
                .apiKey(apiKey)
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(
                        "/edu/creditCard/inquireCardIssuerCodesList",
                        request,
                        new ParameterizedTypeReference<ApiResponse<List<CardIssuerInfoResponse>>>() {})
                .REC();
    }

    /**
     * 사용자의 계좌 목록 전체 조회
     */
    public List<MemberAccountInfo> getMemberAccountList(
            CustomMemberDetails loginMember
    ) throws Exception {

        Member member = findMemberById(loginMember.getMemberId());
        validateMemberHasUserKey(member);
        return fetchMemberAccountList(member);
    }

    private List<MemberAccountInfo> fetchMemberAccountList(Member member) {
        return fetchAssetsFromApi(
                member,
                "inquireDemandDepositAccountList",
                "/edu/demandDeposit/inquireDemandDepositAccountList",
                new ParameterizedTypeReference<ApiResponse<List<MemberAccountInfo>>>() {}
        );
    }

    private List<MemberCardInfo> fetchMemberCardList(Member member) {
        return fetchAssetsFromApi(
                member,
                "inquireSignUpCreditCardList",
                "/edu/creditCard/inquireSignUpCreditCardList",
                new ParameterizedTypeReference<ApiResponse<List<MemberCardInfo>>>() {}
        );
    }

    /**
     * 선택한 은행, 카드사 목록 캐싱
     *
     */
    public void cacheSelectedAsset(AssetConnectRequest request, CustomMemberDetails loginMember) {
        financeCacheService.saveAssetSelection(loginMember.getMemberId(), request);
    }

    /**
     *  1원 송금 요청
     */
    public void requestOpenAccountAuth(
            OpenAccountAuthRequest request,
            CustomMemberDetails loginMember
    ) throws Exception {

        Member member = findMemberById(loginMember.getMemberId());
        validateMemberHasUserKey(member);
        ensureAuthNotLocked(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("openAccountAuth")
                .apiServiceCode("openAccountAuth")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        OpenAccountAuthApiRequest apiRequest = OpenAccountAuthApiRequest.from(
                RequestHeader.args(args),
                request
        );

        finOpenApiClient.post(
                        "/edu/accountAuth/openAccountAuth",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<OpenAccountAuthResponse>>() {});

        // 1원 인증 요청 횟수 카운트
        financeCacheService.checkAccountAuthLimit(member.getMemberId());
    }

    private void ensureAuthNotLocked(Member member) throws IllegalAccessException {
        if(financeCacheService.isAuthLocked(member.getMemberId())) {
            throw new IllegalAccessException("15분간 1원 인증 요청을 할 수 없습니다.");
        }
    }

    /**
     * 1원 송금 인증
     * 성공, 실패 여부 반환
     */
    public CheckAuthResponse checkOpenAccountAuth(
            CheckAuthRequest request,
            CustomMemberDetails loginMember
    ) throws Exception {

        Member member = findMemberById(loginMember.getMemberId());
        validateMemberHasUserKey(member);

        RequestArgs args = RequestArgs.builder()
                .apiName("checkAuthCode")
                .apiServiceCode("checkAuthCode")
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        CheckAuthApiRequest apiRequest = CheckAuthApiRequest.from(
                RequestHeader.args(args),
                request
        );

        // 인증 시도 횟수 카운트
        financeCacheService.checkAuthRetryCount(member.getMemberId());

        CheckAuthApiResponse response = finOpenApiClient.post(
                        "/edu/accountAuth/checkAuthCode",
                        apiRequest,
                        new ParameterizedTypeReference<ApiResponse<CheckAuthApiResponse>>() {})
                .REC();

        // 인증 성공 시 시도 횟수 초기화
        if(response.status().equals("SUCCESS")) {
            financeCacheService.clearAuthRetryCount(member.getMemberId());
        }

        return new CheckAuthResponse(response.status());
    }

    /**
     * 자산 탐색 후 응답
     * DB 에 연결된 자산 저장
     */
    @Transactional
    public FetchedAssetResponse fetchAssets(CustomMemberDetails loginMember) throws Exception {

        Member member = findMemberById(loginMember.getMemberId());
        validateMemberHasUserKey(member);

        // 사용자의 모든 계좌, 카드 목록 조회 (금융망 API)
        List<MemberAccountInfo> memberAccountList = fetchMemberAccountList(member);
        List<MemberCardInfo> memberCardList = fetchMemberCardList(member);

        // 사용자가 선택했던 자산 연결 대상 조회 (레디스)
        AssetConnectRequest selectedAssetCodeByMember = financeCacheService.getAssetSelection(member.getMemberId());

        // [1차 필터링] 선택한 은행, 카드사의 자산만 추출
        List<MemberAccountInfo> selectedAccounts = filterSelectedAssets(
                memberAccountList,
                selectedAssetCodeByMember.bankCodeList(),
                MemberAccountInfo::bankCode
        );

        List<MemberCardInfo> selectedCards = filterSelectedAssets(
                memberCardList,
                selectedAssetCodeByMember.cardIssuerCodeList(),
                MemberCardInfo::cardIssuerCode
        );

        // [2차 필터링] 이미 연동된 계좌, 카드는 제외
        List<MemberAccountInfo> targetAccounts = filterAlreadyLinkedAccounts(member, selectedAccounts);
        List<MemberCardInfo> targetCards = filterAlreadyLinkedCards(member, selectedCards);

        // 연동할 계좌, 카드 정보 저장
        for(MemberAccountInfo accountInfo : targetAccounts) {
            Account linkAccount = Account.createAccount(
                    accountInfo,
                    aesUtil.encrypt(accountInfo.accountNo()),
                    member
            );
            accountRepository.save(linkAccount);
        }

        for(MemberCardInfo cardInfo : targetCards) {
            Card linkCard = Card.createCard(
                    cardInfo,
                    aesUtil.encrypt(cardInfo.cardNo()),
                    member
            );
            cardRepository.save(linkCard);
        }

        // 캐싱된 기록 삭제
        financeCacheService.deleteAssetSelection(member.getMemberId());
        return FetchedAssetResponse.from(targetAccounts, targetCards);
    }

    private List<MemberCardInfo> filterAlreadyLinkedCards(Member member, List<MemberCardInfo> selectedCards) {
        Set<String> cardNos = cardRepository.findCardNoByMemberId(member.getMemberId());

        return selectedCards.stream()
                .filter(card -> {
                    try {
                        return !cardNos.contains(aesUtil.encrypt(card.cardNo()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<MemberAccountInfo> filterAlreadyLinkedAccounts(
            Member member,
            List<MemberAccountInfo> selectedAccounts
    ) {
        Set<String> accountNos = accountRepository.findAccountNoById(member.getMemberId());

        return selectedAccounts.stream()
                .filter(account -> {
                    try {
                        return !accountNos.contains(aesUtil.encrypt(account.accountNo()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private <T> List<T> filterSelectedAssets(List<T> assetList, List<String> codeList, Function<T, String> codeExtractor) {
        Set<String> selectedCodes = new HashSet<>(codeList);
        return assetList.stream()
                .filter(asset -> selectedCodes.contains(codeExtractor.apply(asset)))
                .collect(Collectors.toList());
    }

    private <T> List<T> fetchAssetsFromApi(
            Member member,
            String apiName,
            String path,
            ParameterizedTypeReference<ApiResponse<List<T>>> responseType
    ) {

        RequestArgs args = RequestArgs.builder()
                .apiName(apiName)
                .apiServiceCode(apiName)
                .apiKey(apiKey)
                .userKey(member.getUserKey())
                .build();

        ApiRequest request = new ApiRequest(RequestHeader.args(args));

        return finOpenApiClient.post(path, request, responseType).REC();
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
