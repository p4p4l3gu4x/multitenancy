ALTER TABLE tenants
    ADD CONSTRAINT uq_tenants_tenant_name UNIQUE (tenant_name);
