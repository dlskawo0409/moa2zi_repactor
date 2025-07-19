package com.ssafy.moa2zi.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberNickNameRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Schema(example = "dlskawo0409")
        String nickname
) {
}
