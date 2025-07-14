package com.ssafy.moa2zi.transaction.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transaction_top_spends")
public class TransactionTopSpend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_top_spend_id")
    private long id;

    @Column(nullable = false)
    private Long transactionId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int rankNum;

    private String merchantName;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinate;

    @NotNull
    @Column(name = "transaction_balance")
    private Long balance;

    private TransactionTopSpend (
            Long transactionId,
            Long memberId,
            int rankNum,
            String merchantName,
            Point coordinate,
            Long balance
    ) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.rankNum = rankNum;
        this.merchantName = merchantName;
        this.coordinate = coordinate;
        this.balance = balance;
    }

    public static TransactionTopSpend of(
            Transaction transaction,
            int rankNum
    ) {
        return new TransactionTopSpend(
                transaction.getTransactionId(),
                transaction.getMemberId(),
                rankNum,
                transaction.getMerchantName(),
                transaction.getCoordinate(),
                transaction.getBalance()
        );
    }



}
