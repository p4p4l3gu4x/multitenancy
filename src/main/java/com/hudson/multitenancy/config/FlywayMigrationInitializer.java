package com.hudson.multitenancy.config;

import com.hudson.multitenancy.model.TenantEntity;
import com.hudson.multitenancy.service.TenantService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FlywayMigrationInitializer {

    private final RoutingDataSource routingDataSource;

    @Autowired
    private TenantService tenantService;

    @Autowired
    public FlywayMigrationInitializer(final RoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    @PostConstruct
    public void migrate(){
        migrateMainDatabase();
        migrateTenants();
    }

    private void migrateMainDatabase(){
        String scriptLocation = "db/migration";
        String mainDatabaseName = "multitenancy_main";

        DataSource mainDataSource = routingDataSource.getDefaultDataSource();
        migrate(mainDataSource, mainDatabaseName, scriptLocation);
    }

    private void migrateTenants(){
        List<TenantEntity> tenantEntities = tenantService.listAll();

        tenantEntities.forEach(this::migrateTenant);
    }

    public void migrateTenant(final TenantEntity tenantEntity){
        String scriptLocation = "db/tenant/migration";

        DataSource dataSource = routingDataSource.addDataSourceTenant(tenantEntity);
        String databaseSchema = tenantEntity.getDatabaseSchema();

        migrate(dataSource, databaseSchema, scriptLocation);
    }

    private void migrate(final DataSource dataSource, final String databaseSchema, final String scriptLocation){
        Flyway flyway = Flyway.configure()
            .locations(scriptLocation)
            .baselineOnMigrate(Boolean.TRUE)
            .dataSource(dataSource)
            .schemas(databaseSchema)
            .load();

        flyway.migrate();
    }
}
