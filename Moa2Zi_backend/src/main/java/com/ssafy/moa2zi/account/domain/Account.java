package com.ssafy.moa2zi.account.domain;

import com.ssafy.moa2zi.finance.dto.member.MemberAccountInfo;
import com.ssafy.moa2zi.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 3)
    private String bankCode;

    @Column(nullable = false, length = 20)
    private String bankName;

    @Column(nullable = false)
    private String accountNo; // 암호화

    @Column(nullable = false, length = 20)
    private String accountName;

    @Column(length = 8)
    private String lastTransactionDate;

    @Column(length = 6)
    private String lastTransactionTime;

    private Account(
            Long memberId,
            String bankCode,
            String bankName,
            String accountNo,
            String accountName,
            String lastTransactionDate
    ) {
        this.memberId = memberId;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.lastTransactionDate = lastTransactionDate;
        this.lastTransactionTime = "000000";
    }

    public static Account createAccount(
            MemberAccountInfo accountInfo,
            String encryptedAccountNo,
            Member member
    ) {

        return new Account(
                member.getMemberId(),
                accountInfo.bankCode(),
                accountInfo.bankName(),
                encryptedAccountNo,
                accountInfo.accountName(),
                accountInfo.lastTransactionDate()
        );
    }

    public void updateLastTransactionDate(String lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public void updateLastTransactionTime(String lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }

}
