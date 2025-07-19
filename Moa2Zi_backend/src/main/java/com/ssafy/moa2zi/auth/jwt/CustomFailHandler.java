package com.ssafy.moa2zi.auth.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${basic.login-redirection}") String redirectionUrl;

    public CustomFailHandler(String redirectionUrl) {
        super(redirectionUrl);  // 실패 시 리다이렉트 할 URL 설정
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        logger.warn("OAuth2 로그인 실패: " + exception.getMessage());

        request.getSession().setAttribute("errorMessage", exception.getMessage());

        super.onAuthenticationFailure(request, response, exception);
    }
}
