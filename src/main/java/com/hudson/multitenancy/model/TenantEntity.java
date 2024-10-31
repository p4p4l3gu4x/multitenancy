package com.hudson.multitenancy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants")
public class TenantEntity extends BaseEntity{

    private static final long serialVersionUID = -4452484602406098402L;

    @Column(name = "tenant_name")
    private String tenantName;
    @Column(name = "database_schema")
    private String databaseSchema;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "password")
    private String password;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getDatabaseSchema() {
        return databaseSchema;
    }

    public void setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
