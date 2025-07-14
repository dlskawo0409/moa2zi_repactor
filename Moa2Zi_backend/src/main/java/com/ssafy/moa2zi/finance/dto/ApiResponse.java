package com.ssafy.moa2zi.finance.dto;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.ResponseHeader;

/**
 * 기본 응답 포맷
 */
public record ApiResponse <T>(
        ResponseHeader Header,
        T REC // 실제 필요한 응답 내용
) {
}
