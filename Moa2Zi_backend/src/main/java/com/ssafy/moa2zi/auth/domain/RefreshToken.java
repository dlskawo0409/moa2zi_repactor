package com.ssafy.moa2zi.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {

	@Id
	private String refreshToken;
	private Long memberId;
	private String username;

}