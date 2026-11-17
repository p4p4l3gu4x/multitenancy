package com.hudson.multitenancy.config;

import com.hudson.multitenancy.exception.InvalidTenantNameException;
import java.util.regex.Pattern;

/**
 * Single place that decides what a valid tenant name / schema name is.
 * Tenant names end up in DDL ("CREATE SCHEMA") and in the JDBC URL, so they must never
 * contain anything other than the whitelisted characters.
 */
public final class TenantNames {

    public static final String SCHEMA_PREFIX = "multitenancy_";

    // lower-case letter first, then letters/digits/underscore; 3..31 chars.
    // With the 13 char prefix the schema name stays far below MySQL's 64 char limit.
    private static final Pattern TENANT_NAME = Pattern.compile("^[a-z][a-z0-9_]{2,30}$");
    private static final Pattern SCHEMA_NAME = Pattern.compile("^" + SCHEMA_PREFIX + "[a-z][a-z0-9_]{2,30}$");

    private TenantNames() {
    }

    public static String requireValidTenantName(final String tenantName) {
        if (tenantName == null || !TENANT_NAME.matcher(tenantName).matches()) {
            throw new InvalidTenantNameException(
                "Tenant name must match " + TENANT_NAME.pattern() + " (lower-case letters, digits and '_')");
        }
        return tenantName;
    }

    public static String requireValidSchemaName(final String schemaName) {
        if (schemaName == null || !SCHEMA_NAME.matcher(schemaName).matches()) {
            throw new InvalidTenantNameException("Invalid schema name");
        }
        return schemaName;
    }

    public static String schemaFor(final String tenantName) {
        return SCHEMA_PREFIX + requireValidTenantName(tenantName);
    }

    /** Builds the CREATE SCHEMA statement from an already validated schema name, quoted with backticks. */
    public static String createSchemaSql(final String schemaName) {
        return "CREATE SCHEMA IF NOT EXISTS `" + requireValidSchemaName(schemaName) + "`";
    }
}
