package com.ssafy.moa2zi.pocket_money.dto.request;

import jakarta.validation.constraints.NotNull;

public record PocketMoneyCreateRequest(
        @NotNull
        Long totalAmount,

        @NotNull
        Boolean thisMonthHave
) {
}
