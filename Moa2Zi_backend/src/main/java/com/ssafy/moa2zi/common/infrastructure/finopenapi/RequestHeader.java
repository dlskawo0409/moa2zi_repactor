package com.ssafy.moa2zi.common.infrastructure.finopenapi;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 기관거래고유번호 (institutionTransactionUniqueNo)
 * (YYYYMMDD + HHMMSS + 일련번호 6자리) 또는 20자리 난수
 * API 요청 시 새로운 번호로 임의로 만들어서 넣어주면 됨, 20자 ?
 */

@Getter
@Setter
@ToString
public class RequestHeader {

    private String apiName;                             // API 이름 (호출 API URL 의 마지막 path 명)
    private String transmissionDate;                    // 전송일자(YYYYMMDD)
    private String transmissionTime;                    // 전송시각(HHMMSS)
    private String institutionCode;                   // '00100' 고정
    private String fintechAppNo;               // '001' 고정
    private String apiServiceCode;                          // API 이름 필드와 동일
    private String institutionTransactionUniqueNo;      // 기관별 API 서비스 호출 단위의 고유 코드
    private String apiKey;                              // 발급받은 API KEY
    private String userKey;                             // 앱 사용자가 회원가입할 때 발급받은 USER KEY

    private RequestHeader(
            String apiName,
            String transmissionDate,
            String transmissionTime,
            String institutionTransactionUniqueNo,
            String apiServiceCode,
            String apiKey,
            String userKey
    ) {
        this.apiName = apiName;
        this.transmissionDate = transmissionDate;
        this.transmissionTime = transmissionTime;
        this.institutionCode = "00100";
        this.fintechAppNo = "001";
        this.institutionTransactionUniqueNo = institutionTransactionUniqueNo;
        this.apiServiceCode = apiServiceCode;
        this.apiKey = apiKey;
        this.userKey = userKey;
    }


    public static RequestHeader args(RequestArgs args) {

        // 요청 시 헤더에 필요한 값 생성
        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = getTransmissionDate(now);
        String transmissionTime = getTransmissionTime(now);
        String institutionTransactionUniqueNo = generateInstitutionTransactionUniqueNo(
                transmissionDate, transmissionTime
        );

        return new RequestHeader(
                args.apiName(),
                transmissionDate,
                transmissionTime,
                institutionTransactionUniqueNo,
                args.apiServiceCode(),
                args.apiKey(),
                args.userKey()
        );
    }

    private static String getTransmissionDate(LocalDateTime dateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return dateTime.format(dateFormatter);
    }
    private static String getTransmissionTime(LocalDateTime dateTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        return dateTime.format(timeFormatter);
    }

    private static String generateInstitutionTransactionUniqueNo(
            String transmissionDate,
            String transmissionTime
    ) {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return transmissionDate + transmissionTime + number;
    }

}
