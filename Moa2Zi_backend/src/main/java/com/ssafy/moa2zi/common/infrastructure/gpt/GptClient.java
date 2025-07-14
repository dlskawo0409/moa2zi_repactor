package com.ssafy.moa2zi.common.infrastructure.gpt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GptClient {

    @Value("${openai.completion-url}")
    private String API_URL;

    private final RestTemplate restTemplate;

    public GptResponse send(
            String prompt,
            GptRequestOptions options
    ) {

        // open ai 에게 요청할 body 생성
        List<GptMessage> messages = new ArrayList<>();
        messages.add(new GptMessage("user", prompt));
        GptRequest requestBody = GptRequest.from(messages, options);

        // open ai 요청 후 응답 반환
        return restTemplate.postForObject(API_URL, requestBody, GptResponse.class);
    }

}
