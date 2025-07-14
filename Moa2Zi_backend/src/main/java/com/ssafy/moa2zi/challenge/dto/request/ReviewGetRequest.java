package com.ssafy.moa2zi.challenge.dto.request;

import java.time.LocalDateTime;

public record ReviewGetRequest(
        LocalDateTime lastTime,
        Long next,
        Integer size
) {
    public ReviewGetRequest {
        if(size == null || size == 0) {
            size = 10;
        }
    }
}
