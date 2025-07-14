package com.ssafy.moa2zi.finance.dto.auth;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record CheckAuthApiRequest(
        RequestHeader Header,
        String accountNo,
        String authText,
        String authCode
) {

    public static CheckAuthApiRequest from(
            RequestHeader Header,
            CheckAuthRequest request
    ) {

        return new CheckAuthApiRequest(
                Header,
                request.accountNo(),
                request.authText(),
                request.authCode()
        );
    }
}
