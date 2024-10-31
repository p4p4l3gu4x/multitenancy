CREATE TABLE tenants
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    version            INTEGER           NOT NULL,
    tenant_name        VARCHAR(255)      NOT NULL,
    database_schema    VARCHAR(255)      NOT NULL,
    user_name          VARCHAR(255)              ,
    password           VARCHAR(255)              ,
    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

INSERT INTO tenants (version, tenant_name, database_schema) values(1, "tenant1", "multitenancy_tenant1");

