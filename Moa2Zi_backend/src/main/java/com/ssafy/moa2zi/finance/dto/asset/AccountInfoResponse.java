package com.ssafy.moa2zi.finance.dto.asset;

import com.ssafy.moa2zi.common.util.MaskingUtil;
import com.ssafy.moa2zi.finance.dto.member.MemberAccountInfo;

public record AccountInfoResponse(
        String bankCode,
        String bankName,
        String accountNo,
        String accountName
) {

    public static AccountInfoResponse of(MemberAccountInfo accountInfo) {

        return new AccountInfoResponse(
                accountInfo.bankCode(),
                accountInfo.bankName(),
                MaskingUtil.maskAccountNumber(accountInfo.accountNo()),
                accountInfo.accountName()
        );
    }
}
