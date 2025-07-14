package com.ssafy.moa2zi.common.util;

import org.springframework.stereotype.Component;

@Component
public class MaskingUtil {

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 16) {
            throw new IllegalArgumentException("계좌번호는 16자리여야 합니다.");
        }

        String last4 = accountNumber.substring(12); // 마지막 4자리
        return "************" + last4;        // 앞 12자리 마스킹
    }


    // 마지막 뒤 4자리만 보이도록 마스킹
    public static String maskCardNo(String cardNo) {
        if (cardNo == null || cardNo.length() != 16) {
            throw new IllegalArgumentException("카드번호는 16자리여야 합니다.");
        }

        String last4 = cardNo.substring(12); // 마지막 4자리
        return "************" + last4;        // 앞 12자리 마스킹
    }
}
