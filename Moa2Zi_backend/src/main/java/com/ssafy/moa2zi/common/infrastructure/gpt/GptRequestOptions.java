package com.ssafy.moa2zi.common.infrastructure.gpt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class GptRequestOptions {

    @Builder.Default
    private String model = "gpt-3.5-turbo";
    
    @Builder.Default
    private double temperature = 0.8; // 창의성 증가

    @Builder.Default
    private double frequencyPenalty = 0.0; // 반복 억제

    @Builder.Default
    private double presencePenalty = 0.0; // 새로운 아이디어

    @Builder.Default
    private int maxTokens = 500;

    @Builder.Default
    private String responseFormat = "text";

}
