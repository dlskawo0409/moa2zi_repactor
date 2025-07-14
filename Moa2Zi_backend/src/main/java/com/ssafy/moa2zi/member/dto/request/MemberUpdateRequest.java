package com.ssafy.moa2zi.member.dto.request;

import com.ssafy.moa2zi.member.domain.Disclosure;
import com.ssafy.moa2zi.member.domain.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record MemberUpdateRequest(
        @NotNull
        String nickname,

        @NotNull
        LocalDateTime birthday,

        @NotNull
        Gender gender,

        @NotNull
        String profileImage,

        @NotNull
        Boolean alarm,

        @NotNull
        Disclosure disclosure

) {}
