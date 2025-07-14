package com.ssafy.moa2zi.finance.dto.asset;

import com.ssafy.moa2zi.finance.dto.member.MemberAccountInfo;
import com.ssafy.moa2zi.finance.dto.member.MemberCardInfo;

import java.util.List;

public record FetchedAssetResponse(
        List<AccountInfoResponse> accountList,
        List<CardInfoResponse> cardList
) {

    public static FetchedAssetResponse from(
            List<MemberAccountInfo> accountList,
            List<MemberCardInfo> cardList
    ) {

        return new FetchedAssetResponse(
                accountList.stream().map(AccountInfoResponse::of).toList(),
                cardList.stream().map(CardInfoResponse::of).toList()
        );
    }
}
