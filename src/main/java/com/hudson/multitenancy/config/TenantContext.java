package com.hudson.multitenancy.config;

import com.hudson.multitenancy.model.Tenant;
import java.util.Objects;

public class TenantContext {
    private static final ThreadLocal<String> tenantHolder = new ThreadLocal<>();

    public static String getTenant() {
        String tenant = tenantHolder.get();
        return Objects.isNull(tenant) ? Tenant.DEFAULT.getName() : tenant;
    }

    public static void setTenant(String tenant) {
        tenantHolder.set(tenant);
    }

    public static void clearTenant() {
        tenantHolder.remove();
    }
}
