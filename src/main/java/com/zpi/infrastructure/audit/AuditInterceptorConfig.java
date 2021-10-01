package com.zpi.infrastructure.audit;

import com.zpi.api.authCode.AuthCodeController;
import com.zpi.domain.audit.AuditInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuditInterceptorConfig implements WebMvcConfigurer {
    private final AuditInterceptor auditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor).addPathPatterns("/api" + AuthCodeController.authenticateUri);
    }
}
