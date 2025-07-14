package com.ssafy.moa2zi.sms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SMSValidateRequest(
        @NotBlank
        String phoneNumber,

        @NotNull
        Integer validateNum

){
}
