package com.ssafy.moa2zi.common.infrastructure.finopenapi;

/**
 * 기관거래고유번호 (institutionTransactionUniqueNo)
 * (YYYYMMDD + HHMMSS + 일련번호 6자리) 또는 20자리 난수
 * API 요청 시 새로운 번호로 임의로 만들어서 넣어주면 됨, 20자 ?
 */
public record ResponseHeader(
        String responseCode, // 응답코드
        String responseMessage, // 응답메세지
        String apiName, // API 이름 (호출 API URL 뒷부분)
        String transmissionDate, // 전송일자(YYYYMMDD)
        String transmissionTime, //전송시각(HHMMSS)
        String institutionCode, // '00100' 고정
        String fintechAppNo, // '001' 고정
        String apiService, // API 이름 필드와 동일
        String institutionTransactionUniqueNo // 기관별 API 서비스 호출 단위의 고유 코드
) {
}
