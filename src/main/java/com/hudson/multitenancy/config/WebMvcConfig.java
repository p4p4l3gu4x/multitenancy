package com.hudson.multitenancy.config;

import com.hudson.multitenancy.interceptor.TenantIdentifierInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantIdentifierInterceptor());
    }

    @Bean
    public TenantIdentifierInterceptor tenantIdentifierInterceptor() {
        return new TenantIdentifierInterceptor();
    }
}