
package com.ssafy.moa2zi.sms.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SMSSendRequest(
        @NotBlank(message = "전화번호는 빈칸이 될 수 없습니다.")
        @Size(max = 11, min =11)
        String phoneNumber
) {
}