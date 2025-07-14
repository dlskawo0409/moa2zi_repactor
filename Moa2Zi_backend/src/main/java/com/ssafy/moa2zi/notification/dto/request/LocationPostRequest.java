package com.ssafy.moa2zi.notification.dto.request;

import jakarta.validation.constraints.NotNull;

public record LocationPostRequest (

        @NotNull
        Float latitude,

        @NotNull
        Float longitude
) {
}
