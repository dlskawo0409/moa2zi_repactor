package com.ssafy.moa2zi.finance.dto.asset;

import com.ssafy.moa2zi.common.util.MaskingUtil;
import com.ssafy.moa2zi.finance.dto.member.MemberCardInfo;

public record CardInfoResponse(
        String cardIssuerCode,
        String cardIssuerName,
        String cardNo,
        String cardName
) {

    public static CardInfoResponse of(MemberCardInfo cardInfo) {

        return new CardInfoResponse(
                cardInfo.cardIssuerCode(),
                cardInfo.cardIssuerName(),
                MaskingUtil.maskCardNo(cardInfo.cardNo()),
                cardInfo.cardName()
        );
    }
}
