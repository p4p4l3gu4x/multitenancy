package com.hudson.multitenancy.config;

import com.hudson.multitenancy.interceptor.TenantIdentifierInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RoutingDataSource routingDataSource;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantIdentifierInterceptor())
            // control plane: always runs against the main database, regardless of any tenant header
            .excludePathPatterns("/tenant", "/tenant/**", "/error");
    }

    @Bean
    public TenantIdentifierInterceptor tenantIdentifierInterceptor() {
        return new TenantIdentifierInterceptor(routingDataSource);
    }
}
