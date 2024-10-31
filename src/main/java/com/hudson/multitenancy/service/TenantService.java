package com.hudson.multitenancy.service;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

import com.hudson.multitenancy.config.FlywayMigrationInitializer;
import com.hudson.multitenancy.config.RoutingDataSource;
import com.hudson.multitenancy.model.TenantEntity;
import com.hudson.multitenancy.repository.TenantEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantService {

    private final TenantEntityRepository tenantEntityRepository;

    private EntityManager entityManager;

    private FlywayMigrationInitializer flywayMigrationInitializer;

    private RoutingDataSource routingDataSource;

    @Autowired
    public TenantService(final TenantEntityRepository tenantEntityRepository, final EntityManager entityManager, final FlywayMigrationInitializer flywayMigrationInitializer) {
        this.entityManager = entityManager;
        this.flywayMigrationInitializer = flywayMigrationInitializer;
        this.tenantEntityRepository = tenantEntityRepository;
    }

    public List<TenantEntity> listAll(){
        return tenantEntityRepository.findAll();
    }

    @Transactional
    public TenantEntity createTenant(final String tenantName) {
        String tenantNameInsert = tenantName.trim();
        String tenantSchemaName = String.format("multitenancy_%s", tenantNameInsert);

        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenantName(tenantNameInsert);
        tenantEntity.setDatabaseSchema(tenantSchemaName);

        createTenantSchema(tenantSchemaName);

        tenantEntity = tenantEntityRepository.save(tenantEntity);

        flywayMigrationInitializer.migrateTenant(tenantEntity);

        return tenantEntity;
    }

    @Transactional(REQUIRES_NEW)
    public void createTenantSchema(final String tenantSchemaName) {
        entityManager.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + tenantSchemaName).executeUpdate();
    }
}
