package com.ssafy.moa2zi.transaction.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import com.ssafy.moa2zi.merchant.domain.Merchant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_geohash_code", columnList = "geohash_code")
        }
)
public class Transaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String transactionUniqueNo;

    @NotNull
    private Long memberId;

    @NotNull
    private Long dayId;

    private Long categoryId;

    private String accountNo;

    private String cardNo;

    @NotNull
    @Column(name = "transaction_balance")
    private Long balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NotNull
    @Builder.Default
    private Boolean isInBudget = false;

    @NotNull
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private Emotion emotion;

    @Column(length = 15)
    private String memo;

    @NotNull
    @Column(length = 6)
    private String transactionTime;

    private Long merchantId;

    @Column(length = 100)
    private String merchantName;

    private Integer sidoCode;

    private Integer gugunCode;

    private Integer dongCode;

    private String jibunAddress;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinate;

    @Column(name = "geohash_code", length = 12)
    private String geohashCode; // geohash 적용

    // 감정 등록 메서드
    public void registerEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

}