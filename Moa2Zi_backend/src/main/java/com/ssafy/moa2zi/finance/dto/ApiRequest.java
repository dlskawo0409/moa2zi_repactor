package com.ssafy.moa2zi.finance.dto;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

/**
 * 헤더만 넣는 기본 요청 포맷
 */
public record ApiRequest(
        RequestHeader Header
) {
}
