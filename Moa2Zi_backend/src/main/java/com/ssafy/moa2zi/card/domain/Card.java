package com.ssafy.moa2zi.card.domain;

import com.ssafy.moa2zi.finance.dto.member.MemberCardInfo;
import com.ssafy.moa2zi.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String cardNo; // 암호화

    @Column(nullable = false, length = 3)
    private String cvc;

    @Column(nullable = false, length = 20)
    private String cardUniqueNo;

    @Column(nullable = false, length = 4)
    private String cardIssuerCode;

    @Column(nullable = false, length = 20)
    private String cardIssuerName;

    @Column(nullable = false, length = 100)
    private String cardName;

    @Column(length = 8)
    private String lastTransactionDate;

    @Column(length = 6)
    private String lastTransactionTime;

    private Card(
            Long memberId,
            String cardNo,
            String cvc,
            String cardUniqueNo,
            String cardIssuerCode,
            String cardIssuerName,
            String cardName,
            String lastTransactionDate
    ) {

        this.memberId = memberId;
        this.cardNo = cardNo;
        this.cvc = cvc;
        this.cardUniqueNo = cardUniqueNo;
        this.cardIssuerCode = cardIssuerCode;
        this.cardIssuerName = cardIssuerName;
        this.cardName = cardName;
        this.lastTransactionDate = lastTransactionDate;
        this.lastTransactionTime = "000000";
    }

    public static Card createCard(
            MemberCardInfo cardInfo,
            String encryptedCardNo,
            Member member
    ) {

        String dateTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return new Card(
                member.getMemberId(),
                encryptedCardNo,
                cardInfo.cvc(),
                cardInfo.cardUniqueNo(),
                cardInfo.cardIssuerCode(),
                cardInfo.cardIssuerName(),
                cardInfo.cardName(),
                dateTime
        );
    }

    public void updateLastTransactionDate(String lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public void updateLastTransactionTime(String lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }

}
