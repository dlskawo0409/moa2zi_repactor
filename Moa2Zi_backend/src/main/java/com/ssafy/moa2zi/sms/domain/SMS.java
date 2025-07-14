package com.ssafy.moa2zi.sms.domain;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@RedisHash(value = "sms", timeToLive = 86400)
public class SMS{

    @Id
    private String phoneNumber;

    @Setter
    private Integer count; // 몇번 조회 했는지

    @Setter
    private Integer random; // 6자리 랜덤 변수

    @Setter
    private LocalDateTime limit;
}
