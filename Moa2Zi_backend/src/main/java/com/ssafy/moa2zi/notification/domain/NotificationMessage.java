package com.ssafy.moa2zi.notification.domain;

import com.ssafy.moa2zi.notification.dto.SenderInfo;
import com.ssafy.moa2zi.transaction.domain.TransactionTopSpend;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.locationtech.jts.geom.Point;

@Builder
public record NotificationMessage(
        Long senderId,
        Long receiverId,
        NotificationType notificationType,
        Long dailyPocketMoney,
        Integer increaseRate,
        TopSpend topSpend

) {

    public record TopSpend(
            Long transactionId,
            Long memberId,
            int rankNum,
            String merchantName,
            double latitude,
            double longitude,
            Long balance
    ) {

        public static TopSpend of(TransactionTopSpend topSpend) {

            return new TopSpend(
                    topSpend.getTransactionId(),
                    topSpend.getMemberId(),
                    topSpend.getRankNum(),
                    topSpend.getMerchantName(),
                    topSpend.getCoordinate().getY(),
                    topSpend.getCoordinate().getX(),
                    topSpend.getBalance()
            );
        }

    }
}
