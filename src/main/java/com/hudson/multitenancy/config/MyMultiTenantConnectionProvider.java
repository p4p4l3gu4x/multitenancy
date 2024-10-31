package com.hudson.multitenancy.config;

import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {

    private final RoutingDataSource routingDataSource;

    @Autowired
    public MyMultiTenantConnectionProvider(RoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return routingDataSource.getDefaultDataSource();
    }

    @Override
    protected DataSource selectDataSource(String tenantName) {
        return routingDataSource.getDataSourceByTenant(tenantName);
    }
}
