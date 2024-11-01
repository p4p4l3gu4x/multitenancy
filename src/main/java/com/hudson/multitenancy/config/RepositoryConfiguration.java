package com.hudson.multitenancy.config;

import com.hudson.multitenancy.model.BaseEntity;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"com.hudson.multitenancy.repository"})
@EnableSpringConfigured
@EnableTransactionManagement
public class RepositoryConfiguration {

    @Autowired
    private RoutingDataSource routingDataSource;

    @Bean
    @DependsOn(value = "flywayMigrationInitializer")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPackagesToScan(BaseEntity.class.getPackage().getName());
        entityManagerFactoryBean.setDataSource(routingDataSource.getDefaultDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        entityManagerFactoryBean.setJpaProperties(hibernateProperties());
        entityManagerFactoryBean.setJpaDialect(new HibernateJpaDialect());
        return entityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(org.springframework.orm.jpa.vendor.Database.MYSQL);
        return jpaVendorAdapter;
    }

    @Bean
    public org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider multiTenantConnectionProvider() {
        return new MyMultiTenantConnectionProvider(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, "none");
        properties.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.setProperty(AvailableSettings.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS, "true");
        properties.setProperty("hibernate.jpa.compliance.transaction", "true");
        properties.setProperty("hibernate.jpa.compliance.query", "true");
        properties.setProperty("hibernate.jpa.compliance.list", "true");
        properties.setProperty(AvailableSettings.JPA_ID_GENERATOR_GLOBAL_SCOPE_COMPLIANCE, "true");
        properties.setProperty(AvailableSettings.JPAQL_STRICT_COMPLIANCE, "true");
        properties.setProperty(AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, "true");
        properties.setProperty(AvailableSettings.SHOW_SQL, "false");
        properties.setProperty(AvailableSettings.FORMAT_SQL, "false");
        properties.setProperty(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
        properties.setProperty(AvailableSettings.MAX_FETCH_DEPTH, "4");
        properties.setProperty(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, "16");
        properties.setProperty(AvailableSettings.ORDER_UPDATES, "true");

        //properties.setProperty(AvailableSettings.MULTI_TENANT, "DATABASE");
        properties.setProperty(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, TenantIdentifierResolver.class.getName());
        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider());
        return properties;
    }
}
