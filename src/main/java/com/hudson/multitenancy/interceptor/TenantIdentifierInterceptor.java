package com.hudson.multitenancy.interceptor;

import com.hudson.multitenancy.config.RoutingDataSource;
import com.hudson.multitenancy.config.TenantContext;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Resolves the tenant from the "tenant" header for tenant-scoped endpoints.
 * Control-plane endpoints (/tenant) are excluded in WebMvcConfig and always use the main database.
 */
public class TenantIdentifierInterceptor implements HandlerInterceptor {

    private final RoutingDataSource routingDataSource;

    public TenantIdentifierInterceptor(final RoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String tenant = request.getHeader("tenant");

        if (tenant == null || tenant.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'tenant' header");
            return false;
        }

        if (!routingDataSource.hasTenant(tenant)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown tenant");
            return false;
        }

        TenantContext.setTenant(tenant);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        TenantContext.clearTenant();
    }
}
