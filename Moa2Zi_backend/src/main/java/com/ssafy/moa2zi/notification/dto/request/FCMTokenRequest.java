package com.ssafy.moa2zi.notification.dto.request;

import lombok.Builder;

@Builder
public record FCMTokenRequest(String token) { }
