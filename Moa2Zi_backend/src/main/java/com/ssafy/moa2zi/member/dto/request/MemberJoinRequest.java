package com.ssafy.moa2zi.member.dto.request;

import com.ssafy.moa2zi.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record MemberJoinRequest(

        @NotBlank(message = "이메일은 공백이 될 수 없습니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(example = "dlskawo0409@naver.com")
        String username,

        @NotBlank(message = "비밀번호는 공백이 될 수 없습니다.")
        @Size(min = 9, max = 20, message = "비밀번호는 9자 이상 20자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()-+=<>?]).+$",
                message = "비밀번호는 최소 하나의 소문자, 숫자 및 특수문자를 포함해야 합니다."
        )
        @Schema(example = "dlskawo49!")
        String password,

        @NotBlank(message = "닉네임은 공백이 될 수 없습니다.")
        @Schema(example = "dlskawo0409")
        String nickname,

        @NotNull(message = "생일 정보는 필수 입니다.")
        @Schema(example = "1999-04-09T00:00:00Z")
        LocalDateTime birthday,

        @NotNull(message = "성별은 필수 입니다.")
        @Schema(example = "MALE")
        Gender gender,

        @NotBlank(message = "프로필 이미지는 필수입니다.")
        @Schema(example = "MOUSE.jpg")
        String profileImage,

        @NotNull
        @Size(min = 6, max = 6)
        @Schema(example = "[{\"termId\": 1, \"agree\": true}, {\"termId\": 2, \"agree\": true}, {\"termId\": 3, \"agree\": true}, {\"termId\": 4, \"agree\": true}, {\"termId\": 5, \"agree\": true}, {\"termId\": 6, \"agree\": true}]")
        List<MemberTermRequest> memberTermList,

        @NotBlank
        @Size(min = 11, max = 11)
        String phoneNumber

) {}
