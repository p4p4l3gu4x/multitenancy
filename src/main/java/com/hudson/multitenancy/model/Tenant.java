package com.hudson.multitenancy.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

public enum Tenant {
    DEFAULT(1, "main", "System default database"),
    /*ORGTEST1(2, "orgtest1", "Sample database for organization 1"),
    ORGTEST2(3, "orgtest2", "Sample database for organization 2");*/
    ;

    private Integer id;
    private String name;
    private String description;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    Tenant(final Integer id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Tenant findByName(String name) {
        return Arrays.stream(Tenant.values())
            .filter(t -> t.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(DEFAULT);
    }

    public static Tenant findById(Integer id) {
        return Arrays.stream(Tenant.values())
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElse(DEFAULT);
    }

    @Converter
    public static class TypeConverter implements AttributeConverter<Tenant, Integer> {
        @Override
        public Integer convertToDatabaseColumn(Tenant tenantName) {
            return tenantName != null ? tenantName.getId() : null;
        }

        @Override
        public Tenant convertToEntityAttribute(Integer id) {
            return id != null ? Tenant.findById(id) : null;
        }
    }
}