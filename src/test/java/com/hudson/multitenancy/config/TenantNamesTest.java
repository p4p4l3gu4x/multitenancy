package com.hudson.multitenancy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hudson.multitenancy.exception.InvalidTenantNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TenantNamesTest {

    @ParameterizedTest
    @ValueSource(strings = {"acme", "tenant1", "org_test_2", "main"})
    void acceptsValidNames(String name) {
        assertEquals(name, TenantNames.requireValidTenantName(name));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "", "ab", "Acme", "1acme", "acme corp", "acme-corp", "\"acme\"",
        "x; DROP SCHEMA multitenancy_main", "x`; DROP SCHEMA y; --", "a?b=c", "a&b=c", "a/b", "../x",
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    void rejectsInvalidNames(String name) {
        assertThrows(InvalidTenantNameException.class, () -> TenantNames.requireValidTenantName(name));
    }

    @Test
    void buildsQuotedCreateSchemaSql() {
        assertEquals("CREATE SCHEMA IF NOT EXISTS `multitenancy_acme`",
            TenantNames.createSchemaSql(TenantNames.schemaFor("acme")));
    }

    @Test
    void rejectsSchemaNamesThatAreNotPrefixedAndValid() {
        assertThrows(InvalidTenantNameException.class, () -> TenantNames.createSchemaSql("acme"));
        assertThrows(InvalidTenantNameException.class, () -> TenantNames.createSchemaSql("multitenancy_a`; DROP"));
    }
}
