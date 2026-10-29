package com.bodeguita.bodeguita_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bodeguita.bodeguita_backend.security.ApiKeyInterceptor;
import com.bodeguita.bodeguita_backend.security.SesionInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiKeyInterceptor apiKeyInterceptor;
    private final SesionInterceptor sesionInterceptor;

    public WebConfig(ApiKeyInterceptor apiKeyInterceptor, SesionInterceptor sesionInterceptor) {
        this.apiKeyInterceptor = apiKeyInterceptor;
        this.sesionInterceptor = sesionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/api/**");
    }
}
