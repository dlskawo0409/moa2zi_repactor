package com.ssafy.moa2zi.finance.dto.auth;

public record CheckAuthRequest(
        String accountNo,
        String authText,
        String authCode
) {
}
