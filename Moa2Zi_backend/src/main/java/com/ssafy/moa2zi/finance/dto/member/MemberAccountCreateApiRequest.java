package com.ssafy.moa2zi.finance.dto.member;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record MemberAccountCreateApiRequest(
        RequestHeader Header,
        String accountTypeUniqueNo
) {

    public static MemberAccountCreateApiRequest from(
            RequestHeader Header,
            String accountTypeUniqueNo
    ) {

        return new MemberAccountCreateApiRequest(Header, accountTypeUniqueNo);
    }
}
