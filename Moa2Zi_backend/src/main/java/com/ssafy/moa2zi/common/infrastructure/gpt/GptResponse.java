package com.ssafy.moa2zi.common.infrastructure.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GptResponse(
        String model,
        List<Choice> choices
) {

    public record Choice (
            int index,
            Message message,
            @JsonProperty("finish_reason")
            String finishReason
    ) {
    }

    public record Message (
            String content,
            Boolean refusal
    ){
    }

    public String getContent() {
        return choices.get(0).message().content();
    }
}
