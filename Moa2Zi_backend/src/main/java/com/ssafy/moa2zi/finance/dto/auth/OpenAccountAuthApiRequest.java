package com.ssafy.moa2zi.finance.dto.auth;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record OpenAccountAuthApiRequest(
        RequestHeader Header,
        String accountNo,
        String authText
) {

    public static OpenAccountAuthApiRequest from(
            RequestHeader Header,
            OpenAccountAuthRequest request
    ) {

        return new OpenAccountAuthApiRequest(
                Header,
                request.accountNo(),
                request.authText()
        );
    }
}
