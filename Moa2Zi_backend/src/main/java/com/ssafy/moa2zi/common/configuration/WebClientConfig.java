package com.ssafy.moa2zi.common.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${fin-open-api.url}")
    private String finOpenApiUrl;

    @Value("${category-ai-model.url}")
    private String modelApiUrl;

    @Bean
    @Primary
    public WebClient finOpenApiWebClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024)) // 10MB로 증가
                        .build())

                .baseUrl(finOpenApiUrl)
                .build();
    }

    @Bean
    public WebClient modelApiWebClient() {
        return WebClient.builder()
                .baseUrl(modelApiUrl)
                .build();
    }
}
