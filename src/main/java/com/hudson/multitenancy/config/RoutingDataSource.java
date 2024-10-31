package com.hudson.multitenancy.config;

import com.hudson.multitenancy.model.TenantEntity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

@Component
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final Map<Object, Object> dataSourceMap = new HashMap<>();
    public static final String MAIN_TENANT_NAME = "main";

    private final DatabaseConfiguration databaseConfiguration;

    @Autowired
    public RoutingDataSource(final DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenant();
    }

    @PostConstruct
    public void initMainDataSource() {
        HikariDataSource hirakiDataSourceMain = createHirakiDataSourceMain();
        dataSourceMap.put(MAIN_TENANT_NAME, hirakiDataSourceMain);
        setDefaultTargetDataSource(getDefaultDataSource());

        setTargetDataSources(dataSourceMap);
    }

    public HikariDataSource addDataSourceTenant(final TenantEntity tenant){
        try {
            Connection mainConnection = getDefaultDataSource().getConnection();
            mainConnection.prepareStatement(String.format("CREATE SCHEMA IF NOT EXISTS %s", tenant.getDatabaseSchema())).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String tenantName = tenant.getTenantName();
        HikariDataSource hirakiDataSourceTenant = createHirakiDataSourceTenant(tenantName);
        dataSourceMap.put(tenantName, hirakiDataSourceTenant);

        return hirakiDataSourceTenant;
    }

    public DataSource getDataSourceByTenant(final String tenantName) {
        return (DataSource) dataSourceMap.get(tenantName);
    }

    public DataSource getDefaultDataSource() {
        return getDataSourceByTenant(MAIN_TENANT_NAME);
    }

    private HikariDataSource createHirakiDataSourceTenant(String tenantName) {
        return new HikariDataSource(createHirakiConfig(tenantName));
    }
    private HikariDataSource createHirakiDataSourceMain() {
        return createHirakiDataSourceTenant(MAIN_TENANT_NAME);
    }

    private HikariConfig createHirakiConfig(final String tenantName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(databaseConfiguration.getUrl().replace("tenantName", tenantName));
        hikariConfig.setUsername(databaseConfiguration.getUser());
        hikariConfig.setPassword(databaseConfiguration.getPassword());
        hikariConfig.setAutoCommit(Boolean.FALSE);
        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", Boolean.TRUE);
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", Boolean.TRUE);
        hikariConfig.addDataSourceProperty("dataSource.useLocalSessionState", Boolean.TRUE);
        hikariConfig.addDataSourceProperty("dataSource.rewriteBatchedStatements", Boolean.TRUE);
        hikariConfig.addDataSourceProperty("dataSource.cacheResultSetMetadata", Boolean.TRUE);
        hikariConfig.addDataSourceProperty("dataSource.cacheServerConfiguration", Boolean.TRUE);
        hikariConfig.addDataSourceProperty("dataSource.maintainTimeStats", Boolean.FALSE);
        return hikariConfig;
    }
}
