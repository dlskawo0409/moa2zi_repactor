package com.ssafy.moa2zi.auth.jwt;

import com.ssafy.moa2zi.auth.application.RedisRefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Objects;
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
//    private final RefreshRepository refreshRepository;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final String refreshTokenName;
    private final String oauthTokenName;

//    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
//        this.jwtUtil = jwtUtil;
//        this.refreshRepository = refreshRepository;
//    }

    public CustomLogoutFilter(JWTUtil jwtUtil,
                              RedisRefreshTokenService redisRefreshTokenService,
                              String refreshTokenName,
                              String oauthTokenName){
        this.jwtUtil = jwtUtil;
        this.redisRefreshTokenService = redisRefreshTokenService;
        this.refreshTokenName = refreshTokenName;
        this.oauthTokenName = oauthTokenName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        String expectedUri = request.getContextPath() + "/logout";
        if (!requestUri.equals(expectedUri)) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie refresh = null;
        Cookie authorization = null;
        Cookie jsessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // 쿠키 배열이 null인지 확인
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(oauthTokenName)) {
                    authorization = cookie;
                }
                if(cookie.getName().equals(refreshTokenName)){
                    refresh = cookie;
                }
                if(cookie.getName().equals("JSESSIONID")){
                    jsessionId = cookie;
                }
            }
        }

        if(authorization != null){
            authorization.setMaxAge(0);
            authorization.setPath("/");
            response.addCookie(authorization);
        }
        if(jsessionId != null){
            jsessionId.setMaxAge(0);
            jsessionId.setPath("/");
            response.addCookie(jsessionId);
        }
        if(refresh != null){
            System.out.println("refresh is not null");
            redisRefreshTokenService.deleteRefreshToken(refresh.getValue());
            refresh.setPath("/");
            refresh.setMaxAge(0);
            response.addCookie(refresh);
        }

    }
}
