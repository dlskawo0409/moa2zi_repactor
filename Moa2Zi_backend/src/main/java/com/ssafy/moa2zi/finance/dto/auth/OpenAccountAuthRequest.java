package com.ssafy.moa2zi.finance.dto.auth;

public record OpenAccountAuthRequest(
        String accountNo,
        String authText
) {
}
