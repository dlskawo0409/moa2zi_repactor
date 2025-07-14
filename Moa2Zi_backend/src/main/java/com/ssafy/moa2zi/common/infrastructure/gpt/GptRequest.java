package com.ssafy.moa2zi.common.infrastructure.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GptRequest(
        String model,
        List<GptMessage> messages,
        double temperature, // creativity of generated completions
        @JsonProperty("max_tokens")
        int maxTokens, // maximum generated tokens
        @JsonProperty("frequency_penalty")
        double frequencyPenalty,
        @JsonProperty("response_format")
        Type responseFormat
) {

    public static record Type (
            String type
    ) {}

    public static GptRequest from(
            List<GptMessage> messages,
            GptRequestOptions options
    ) {

        return new GptRequest(
                options.getModel(),
                messages,
                options.getTemperature(),
                options.getMaxTokens(),
                options.getFrequencyPenalty(),
                new Type(options.getResponseFormat())
        );
    }
}
