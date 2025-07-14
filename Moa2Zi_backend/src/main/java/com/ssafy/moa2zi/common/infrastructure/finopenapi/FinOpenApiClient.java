package com.ssafy.moa2zi.common.infrastructure.finopenapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class FinOpenApiClient {

    @Qualifier("finOpenApiWebClient")
    private final WebClient webClient;

    /**
     *
     * @param path : 요청 주소
     * @param data : body 값
     * @param responseType : 응답받을 dto
     */
    public <T, R> R post(
            String path,
            T data,
            ParameterizedTypeReference<R> responseType // R 부분에 응답받을 dto 지정
    ) {


        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

}
