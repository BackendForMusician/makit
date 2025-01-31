package com.example.makit.config;

import com.example.makit.login.Interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor) // 인터셉터 등록
                .addPathPatterns("/api/auth/logout")
                .addPathPatterns("/api/profile/**"); // 인터셉터가 적용될 경로
                //.excludePathPatterns("/api/auth/**", "/error"); // 제외 경로
    }
}
