package com.example.makit.login.Interceptor;

import com.example.makit.login.Util.SessionUtil;
import com.example.makit.signup.Entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserEntity loginUser = SessionUtil.getUserFromSession(request.getSession(false));
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false; // 요청 중단
        }
        return true; // 인증된 요청만 진행
    }
}