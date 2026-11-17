package com.hudson.multitenancy.config;

import com.hudson.multitenancy.model.TenantEntity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

@Component
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
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
        String tenantName = TenantNames.requireValidTenantName(tenant.getTenantName());
        String createSchemaSql = TenantNames.createSchemaSql(tenant.getDatabaseSchema());

        // try-with-resources: the connection must go back to the main pool
        try (Connection mainConnection = getDefaultDataSource().getConnection();
             Statement statement = mainConnection.createStatement()) {
            statement.executeUpdate(createSchemaSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // reuse an existing pool instead of overwriting (and leaking) it
        return (HikariDataSource) dataSourceMap.computeIfAbsent(tenantName, this::createHirakiDataSourceTenant);
    }

    public boolean hasTenant(final String tenantName) {
        return tenantName != null
            && !MAIN_TENANT_NAME.equals(tenantName)
            && dataSourceMap.containsKey(tenantName);
    }

    public DataSource getDataSourceByTenant(final String tenantName) {
        return (DataSource) dataSourceMap.get(tenantName);
    }

    public DataSource getDefaultDataSource() {
        return getDataSourceByTenant(MAIN_TENANT_NAME);
    }

    private HikariDataSource createHirakiDataSourceTenant(Object tenantName) {
        return new HikariDataSource(createHirakiConfig((String) tenantName));
    }
    private HikariDataSource createHirakiDataSourceMain() {
        return createHirakiDataSourceTenant(MAIN_TENANT_NAME);
    }

    private HikariConfig createHirakiConfig(final String tenantName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // tenant name is substituted into the JDBC URL: only whitelisted characters may reach it
        hikariConfig.setJdbcUrl(databaseConfiguration.getUrl().replace("tenantName", TenantNames.requireValidTenantName(tenantName)));
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
