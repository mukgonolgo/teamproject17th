package com.test.project.user;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException {
        // 예외 메시지를 명확하게 설정
        String errorMessage;

        if (exception.getMessage().equals("관리자의 승인이 필요합니다.")) {
            errorMessage = "관리자의 승인이 필요합니다.";
        } else {
            errorMessage = "자격 증명에 실패하였습니다.";  // 기본 메시지
        }

        // 오류 메시지를 인코딩하여 URL로 전달
        response.sendRedirect("/user/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
    }
}