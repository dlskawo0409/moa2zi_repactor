package com.ssafy.moa2zi.common.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@Primary
public class CorsConfig implements CorsConfigurationSource {

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 오리진 설정 (클라이언트 도메인으로 변경하세요)
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "http://localhost:8080","http://localhost:80",
                "https://70.12.247.113:5173",
                "http://70.12.247.114:5173",
                "https://70.12.247.115:5173",
                "https://70.12.247.116:5173",
                "https://70.12.247.117:5173",
                "http://70.12.247.117:5173",
                "http://70.12.247.118:5173",
                "https://70.12.246.158:5173",
                "http://70.12.246.158:5173"
        ));


        // 허용할 HTTP 메서드 설정
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 허용할 헤더 설정
        config.setAllowedHeaders(Arrays.asList("*"));

        // 노출할 헤더 설정 (클라이언트에서 접근 가능한 헤더)
        config.setExposedHeaders(Arrays.asList("access"));

        // 인증 정보 허용 여부 설정
        config.setAllowCredentials(true);

        return config;
    }
}
