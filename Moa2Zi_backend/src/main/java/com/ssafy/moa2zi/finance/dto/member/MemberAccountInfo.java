package com.ssafy.moa2zi.finance.dto.member;

public record MemberAccountInfo(
       String bankCode,
       String bankName,
       String username,
       String accountNo,
       String accountName,
       String accountTypeCode,
       String accountTypeName,
       String accountCreatedDate,
       String accountExpiryDate,
       Long dailyTransferLimit,
       Long oneTimeTransferLimit,
       Long accountBalance,
       String lastTransactionDate,
       String currency
) {

}
