package com.hudson.multitenancy.interceptor;

import com.hudson.multitenancy.config.TenantContext;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class TenantIdentifierInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String tenantNameHeader = request.getHeader("tenant");

        String tenant = tenantNameHeader;
        TenantContext.setTenant(tenant);

        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        TenantContext.clearTenant();
    }
}