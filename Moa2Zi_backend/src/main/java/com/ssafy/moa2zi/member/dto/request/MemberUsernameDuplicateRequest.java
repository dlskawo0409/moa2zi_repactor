package com.ssafy.moa2zi.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberUsernameDuplicateRequest(
        @NotBlank(message = "이메일은 공백이 될 수 없습니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(example = "dlskawo0409@naver.com")
        String username
) {}
